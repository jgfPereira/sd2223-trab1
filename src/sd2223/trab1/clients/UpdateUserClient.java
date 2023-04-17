package sd2223.trab1.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import sd2223.trab1.api.User;

public class UpdateUserClient {

	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 6) {
			System.err.println("Use: java aula2.clients.UpdateUserClient url name oldpwd displayName domain password");
			return;
		}
		String serverUrl = args[0];
		String name = args[1];
		String oldpwd = args[2];
		String displayName = args[3];
		String domain = args[4];
		String password = args[5];
		var u = new User(name, displayName, domain, password);

		Log.info("Sending request to server.");
//		var result = new RestUsersClient(URI.create(serverUrl)).updateUser(name, oldpwd, u);
//		System.out.println("Result: " + result);
	}
}
