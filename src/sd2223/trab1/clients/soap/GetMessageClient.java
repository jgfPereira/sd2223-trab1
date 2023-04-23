package sd2223.trab1.clients.soap;

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
        var soapClient = new SoapFeedsClient(domain);
        var res = soapClient.getMessage(user, mid);
        System.out.println(res);
        Log.info(res.toString());
    }
}
