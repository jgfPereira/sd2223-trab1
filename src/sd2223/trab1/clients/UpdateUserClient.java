package sd2223.trab1.clients;

import sd2223.trab1.api.User;

import java.io.IOException;
import java.util.logging.Logger;

public class UpdateUserClient {

    private static final Logger Log = Logger.getLogger(UpdateUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 6) {
            System.err.println("Use: java sd2223.trab1.clients.UpdateUserClient name oldpwd oldDomain displayName domain password");
            return;
        }
        String name = args[0];
        String oldpwd = args[1];
        String oldDomain = args[2];
        String displayName = args[3];
        String domain = args[4];
        String password = args[5];
        var updatedUser = new User(name, password, domain, displayName);
        Log.info("Sending request to server.");
        var result = new RestUsersClient(oldDomain).updateUser(name, oldpwd, updatedUser);
        if (result != null) {
            Log.info(result.toString());
        }
        System.out.println("Result: " + result);
    }
}
