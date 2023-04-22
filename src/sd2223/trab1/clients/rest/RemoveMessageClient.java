package sd2223.trab1.clients.rest;

import java.util.logging.Logger;

public class RemoveMessageClient {

    private static final Logger Log = Logger.getLogger(RemoveMessageClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Use: java sd2223trab1.clients.rest.RemoveMessageClient user mid pwd");
            return;
        }
        String user = args[0];
        long mid = Long.parseLong(args[1]);
        String pwd = args[2];
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        new RestFeedsClient(domain).removeFromPersonalFeed(user, mid, pwd);
    }
}
