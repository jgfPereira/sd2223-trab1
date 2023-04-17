package sd2223.trab1.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import sd2223.trab1.api.User;

public class CreateUserClient {

    private static final Logger Log = Logger.getLogger(CreateUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 5) {
            System.err.println("Use: java trab1.clients.CreateUserClient url name pwd domain displayName");
            return;
        }
        String serverUrl = args[0];
        String name = args[1];
        String pwd = args[2];
        String domain = args[3];
        String displayName = args[4];
        User u = new User(name, pwd, domain, displayName);
        Log.info("Sending request to server.");
        var result = new RestUsersClient(URI.create(serverUrl), domain).createUser(u);
        Log.info(result);
        System.out.println("Result: " + result);
    }
}
