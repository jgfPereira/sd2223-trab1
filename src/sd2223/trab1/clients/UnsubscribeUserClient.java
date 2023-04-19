package sd2223.trab1.clients;

import java.io.IOException;
import java.util.logging.Logger;

public class UnsubscribeUserClient {

    private static final Logger Log = Logger.getLogger(UnsubscribeUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Use: java sd2223trab1.clients.UnsubscribeUserClient user userSub pwd");
            return;
        }
        String user = args[0];
        String userSub = args[1];
        String pwd = args[2];
        final String domain = user.split("@")[1];
        Log.info("Sending request to server.");
        new RestFeedsClient(domain).unsubscribeUser(user, userSub, pwd);
    }
}
