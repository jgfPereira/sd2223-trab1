package sd2223.trab1.clients.soap;

import java.io.IOException;
import java.util.logging.Logger;

public class ListSubscribersClient {

    private static final Logger Log = Logger.getLogger(ListSubscribersClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: user");
            return;
        }
        String user = args[0];
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        var soapClient = new SoapFeedsClient(domain);
        var res = soapClient.listSubs(user);
        System.out.println(res);
        Log.info(res.toString());
    }
}