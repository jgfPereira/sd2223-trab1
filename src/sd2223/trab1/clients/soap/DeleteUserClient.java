package sd2223.trab1.clients.soap;

import java.io.IOException;
import java.util.logging.Logger;

public class DeleteUserClient {

    private static final Logger Log = Logger.getLogger(DeleteUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: name pwd domain");
            return;
        }
        String name = args[0];
        String pwd = args[1];
        String domain = args[2];
        var soapClient = new SoapUsersClient(domain);
        Log.info("Sending request to server.");
        var res = soapClient.deleteUser(name, pwd);
        System.out.println(res);
        Log.info(res.toString());
    }
}
