package sd2223.trab1.clients.rest;

import java.io.IOException;
import java.util.logging.Logger;

public class SearchUsersClient {

    private static final Logger Log = Logger.getLogger(SearchUsersClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Use: java sd2223.clients.rest.SearchUsersClient pattern domain");
            return;
        }
        String pattern = args[0];
        String domain = args[1];
        var result = new RestUsersClient(domain).searchUsers(pattern);
        if (result != null) {
            Log.info(result.toString());
        }
        System.out.println("Result: " + result);
    }
}
