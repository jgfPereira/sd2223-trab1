package sd2223.trab1.clients.soap;

import java.util.logging.Logger;

public class GetUserClient {

    private static final Logger Log = Logger.getLogger(GetUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("usage: name pwd domain");
            System.exit(0);
        }
        var name = args[0];
        var pwd = args[1];
        var domain = args[2];
        var soapClient = new SoapUsersClient(domain);
        Log.info("Sending request to server.");
        var res = soapClient.getUser(name, pwd);
        System.out.println(res);
        Log.info(res.toString());
    }

}
