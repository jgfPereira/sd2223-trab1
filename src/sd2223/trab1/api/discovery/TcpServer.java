package sd2223.trab1.api.discovery;

import java.net.InetAddress;
import java.net.ServerSocket;

public class TcpServer {

    private static final int PORT = 9000;
    private static final int BUF_SIZE = 2048;
    private static final String SERVICE_NAME = "ChatWebService";

    public static void main(String[] args) throws Exception {
        // (tcp://hostname:port) - URI Format
        Discovery discovery = Discovery.getInstance();
        final String HOST = InetAddress.getLocalHost().getHostName();
        final String SERVICE_URI = String.format("http://%s:%d/index.html", HOST, PORT);
        discovery.announce(SERVICE_NAME, SERVICE_URI);
        try (var ss = new ServerSocket(PORT)) {
            System.err.println("Accepting connections at: " + ss.getLocalSocketAddress());
            while (true) {
                var cs = ss.accept();
                System.err.println("Accepted connection from client at: " + cs.getRemoteSocketAddress());

                int n;
                var buf = new byte[BUF_SIZE];
                while ((n = cs.getInputStream().read(buf)) > 0)
                    System.out.write(buf, 0, n);

                cs.close();
                System.err.println("Connection closed.");
            }
        }
    }
}