package sd2223.trab1.clients.soap;

import sd2223.trab1.api.Message;

import java.io.IOException;
import java.util.logging.Logger;

public class PostMessageClient {

    private static final Logger Log = Logger.getLogger(PostMessageClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: user pwd msgText");
            return;
        }
        String user = args[0];
        String pwd = args[1];
        String msgText = args[2];
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        Message msg = new Message(-1, user, domain, msgText);
        var soapClient = new SoapFeedsClient(domain);
        var res = soapClient.postMessage(user, pwd, msg);
        System.out.println(res);
        Log.info(res.toString());
    }
}
