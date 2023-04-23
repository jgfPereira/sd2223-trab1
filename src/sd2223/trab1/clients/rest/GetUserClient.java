package sd2223.trab1.clients.rest;

import java.io.IOException;
import java.util.logging.Logger;

public class GetUserClient {

    private static final Logger Log = Logger.getLogger(GetUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: name password domain");
            return;
        }
        String name = args[0];
        String password = args[1];
        String domain = args[2];
        Log.info("Sending request to server");
        var result = new RestUsersClient(domain).getUser(name, password);
        if (result != null) {
            Log.info(result.toString());
        }
        System.out.println("Result: " + result);
    }

}
