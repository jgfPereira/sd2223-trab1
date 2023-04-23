package sd2223.trab1.clients.soap;

import java.util.logging.Logger;

public class GetMessagesClient {

    private static final Logger Log = Logger.getLogger(GetMessagesClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: user time");
            return;
        }
        String user = args[0];
        long time = Long.parseLong(args[1]);
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        var soapClient = new SoapFeedsClient(domain);
        var res = soapClient.getMessages(user, time);
        System.out.println(res);
    }
}
