package sd2223.trab1.clients;

import sd2223.trab1.api.User;

import java.io.IOException;
import java.util.logging.Logger;

public class CreateUserClient {

    private static final Logger Log = Logger.getLogger(CreateUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.err.println("Use: java sd2223trab1.clients.CreateUserClient name pwd domain displayName");
            return;
        }
        String name = args[0];
        String pwd = args[1];
        String domain = args[2];
        String displayName = args[3];
        User u = new User(name, pwd, domain, displayName);
        Log.info("Sending request to server.");
        var result = new RestUsersClient(domain).createUser(u);
        Log.info(result);
        System.out.println("Result: " + result);
    }
}
