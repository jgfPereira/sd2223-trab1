package sd2223.trab1.server.soap;

import jakarta.xml.ws.Endpoint;
import sd2223.trab1.api.discovery.Discovery;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoapFeedsServer {

    public static final int PORT = 8083;
    public static final String SERVICE_TYPE = "feeds";
    private static final String SEPARATOR = ":";
    private static final Logger Log = Logger.getLogger(SoapFeedsServer.class.getName());
    private static final String SERVER_URI_FMT = "http://%s:%s/soap";

    public static void main(String[] args) throws Exception {

//		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
//		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
//		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
//		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

        String domain = args[0];
        Log.setLevel(Level.INFO);

        final String ip = InetAddress.getLocalHost().getHostAddress();
        final String serviceName = domain + SEPARATOR + SERVICE_TYPE;
        final String serverURI = String.format(SERVER_URI_FMT, ip, PORT);

        Endpoint.publish(serverURI.replace(ip, "0.0.0.0"), new SoapFeedsWebService());
        Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE_TYPE, serverURI));

        Discovery discovery = Discovery.getInstance();
        discovery.announce(serviceName, serverURI);
    }
}
