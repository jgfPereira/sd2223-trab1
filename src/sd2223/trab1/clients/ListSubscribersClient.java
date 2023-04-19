package sd2223.trab1.clients;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ListSubscribersClient {

    private static final Logger Log = Logger.getLogger(ListSubscribersClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Use: java sd2223trab1.clients.ListSubscribersClient user");
            return;
        }
        String user = args[0];
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        List<String> userSubs = new RestFeedsClient(domain).listSubs(user);
        Log.info(userSubs.toString());
        System.out.println("Success, list of subscribers: " + userSubs);
    }
}
