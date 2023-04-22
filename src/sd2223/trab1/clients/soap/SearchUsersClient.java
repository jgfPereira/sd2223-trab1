package sd2223.trab1.clients.soap;

import java.io.IOException;

public class SearchUsersClient {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: pattern domain");
            return;
        }
        String pattern = args[0];
        String domain = args[1];
        var soapClient = new SoapUsersClient(domain);
        var res = soapClient.searchUsers(pattern);
        System.out.println(res);
    }
}
