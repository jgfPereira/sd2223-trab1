package sd2223.trab1.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetUserClient {

	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.err.println("Use: java aula2.clients.GetUserClient url name password");
			return;
		}
		String serverUrl = args[0];
		String name = args[1];
		String password = args[2];
		Log.info("Sending request to server.");
		var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, password);
		System.out.println("Result: " + result);
	}

}
