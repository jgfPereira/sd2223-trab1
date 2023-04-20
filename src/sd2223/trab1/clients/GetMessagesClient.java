package sd2223.trab1.clients;

import sd2223.trab1.api.Message;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class GetMessagesClient {

    private static final Logger Log = Logger.getLogger(GetMessagesClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Use: java sd2223trab1.clients.GetMessagesClient user time");
            return;
        }
        String user = args[0];
        long time = Long.parseLong(args[1]);
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        List<Message> allFeed = new RestFeedsClient(domain).getMessages(user, time);
        if (allFeed != null) {
            Log.info(allFeed.toString());
            System.out.println("Success, feed (" + allFeed.size() + " messages): " + allFeed);
        } else {
            Log.info("List is null, some error occurred");
        }
    }
}
