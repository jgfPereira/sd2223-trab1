package sd2223.trab1.server;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd2223.trab1.api.discovery.Discovery;
import sd2223.trab1.server.resources.UsersResource;
import sd2223.trab1.server.util.CustomLoggingFilter;

public class UsersServer {

	private static Logger Log = Logger.getLogger(UsersServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	private static final String SEPARATOR = ":";
	public static final int PORT = 8080;
	public static final String SERVICE_TYPE = "users";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		String domain = args[0];
		try {
			ResourceConfig config = new ResourceConfig();
			config.register(UsersResource.class);
			config.register(CustomLoggingFilter.class);

			final String ip = InetAddress.getLocalHost().getHostAddress();
			final String serviceName = domain + SEPARATOR + SERVICE_TYPE;
			final String serverURI = String.format(SERVER_URI_FMT, ip, PORT);

			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);
			Log.info(String.format("%s Server ready @ %s\n", SERVICE_TYPE, serverURI));

			Discovery discovery = Discovery.getInstance();
			discovery.announce(serviceName, serverURI);

		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}
}
