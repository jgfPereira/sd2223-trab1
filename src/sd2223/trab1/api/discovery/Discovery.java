package sd2223.trab1.api.discovery;

import java.net.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public interface Discovery {

    static Discovery getInstance() {
        return DiscoveryImpl.getInstance();
    }

    void announce(String serviceName, String serviceURI);

    URI[] knownUrisOf(String serviceName, int minReplies);

    URI[] knownUrisOf(String serviceName);

    String[] getHostAndPortOfService(String serviceName) throws MalformedURLException;

    void printServiceInfo(String serviceName);

    Map<String, ServiceInfo> getServicesFound();
}

class DiscoveryImpl implements Discovery {

    static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("231.7.8.9", 2317);
    private static final int DISCOVERY_RETRY_TIMEOUT = 5000;
    private static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;
    private static final String DELIMITER = "\t";
    private static final int MAX_DATAGRAM_SIZE = 65536;
    private static final Logger Log = Logger.getLogger(Discovery.class.getName());
    private static Discovery singleton;

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
    }

    private final Map<String, ServiceInfo> servicesFound;

    private DiscoveryImpl() {
        this.servicesFound = new HashMap<>();
        this.startListener();
    }

    synchronized static Discovery getInstance() {
        if (singleton == null) {
            singleton = new DiscoveryImpl();
        }
        return singleton;
    }

    public Map<String, ServiceInfo> getServicesFound() {
        return this.servicesFound;
    }

    public String[] getHostAndPortOfService(String serviceName) throws MalformedURLException {
        ServiceInfo si = this.servicesFound.get(serviceName);
        if (si == null) {
            this.knownUrisOf(serviceName);
            si = this.servicesFound.get(serviceName);
        }
        return si.getHostAndPort();
    }

    public void printServiceInfo(String serviceName) {
        ServiceInfo si = this.servicesFound.get(serviceName);
        if (si == null) {
            System.out.println("Service does not exist");
            return;
        }
        si.printAllUris(serviceName);
    }

    @Override
    public void announce(String serviceName, String serviceURI) {
        Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName,
                serviceURI));
        var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();
        var pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
        new Thread(() -> {
            try (var ds = new DatagramSocket()) {
                while (true) {
                    try {
                        ds.send(pkt);
                        Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public URI[] knownUrisOf(String serviceName, int minEntries) {
        URI[] urisOfService;
        synchronized (this.servicesFound) {
            ServiceInfo si = this.servicesFound.get(serviceName);
            while (si == null) {
                try {
                    this.servicesFound.wait(DISCOVERY_RETRY_TIMEOUT);
                    si = this.servicesFound.get(serviceName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<URI> uriList = si.getAllUris();
            while (uriList.size() < minEntries) {
                try {
                    this.servicesFound.wait();
                    uriList = si.getAllUris();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            urisOfService = new URI[uriList.size()];
            urisOfService = uriList.toArray(urisOfService);
        }
        return urisOfService;
    }

    public URI[] knownUrisOf(String serviceName) {
        URI[] urisOfService;
        synchronized (this.servicesFound) {
            ServiceInfo si = this.servicesFound.get(serviceName);
            while (si == null) {
                try {
                    this.servicesFound.wait(DISCOVERY_RETRY_TIMEOUT);
                    si = this.servicesFound.get(serviceName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<URI> uriList = si.getAllUris();
            urisOfService = new URI[uriList.size()];
            urisOfService = uriList.toArray(urisOfService);
        }
        return urisOfService;
    }

    private void startListener() {
        Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(),
                DISCOVERY_ADDR.getPort()));
        new Thread(() -> {
            try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
                ms.joinGroup(DISCOVERY_ADDR, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
                for (; ; ) {
                    try {
                        var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);
                        ms.receive(pkt);
                        var msg = new String(pkt.getData(), 0, pkt.getLength());
                        Log.info(String.format("Received: %s\n", msg));
                        System.out.printf("FROM %s (%s)\n", pkt.getAddress().getCanonicalHostName(),
                                pkt.getAddress().getHostAddress());
                        var parts = msg.split(DELIMITER);
                        if (parts.length == 2) {
                            var serviceName = parts[0];
                            var uri = URI.create(parts[1]);
                            synchronized (this.servicesFound) {
                                this.servicesFound.putIfAbsent(serviceName, new ServiceInfo());
                                ServiceInfo si = this.servicesFound.get(serviceName);
                                List<URI> serviceUris = si.getAllUris();
                                if (!serviceUris.contains(uri)) {
                                    si.addEntry(new URIEntry(uri, new Timestamp(System.currentTimeMillis())));
                                    this.servicesFound.notifyAll();
                                }
                            }
                        }
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }).start();
    }
}