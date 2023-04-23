package sd2223.trab1.clients.rest;

import sd2223.trab1.api.Message;

import java.util.logging.Logger;

public class GetMessageClient {

    private static final Logger Log = Logger.getLogger(GetMessageClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: user mid");
            return;
        }
        String user = args[0];
        long mid = Long.parseLong(args[1]);
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        Message msg = new RestFeedsClient(domain).getMessage(user, mid);
        if (msg != null) {
            Log.info(msg.toString());
            System.out.println("Success, fetched msg: " + msg);
        } else {
            Log.info("Message is null, some error occurred");
            System.out.println("Could not fetch message");
        }
    }
}
