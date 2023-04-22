package sd2223.trab1.clients;

import sd2223.trab1.api.java.Users;
import sd2223.trab1.clients.soap.SoapUsersClient;

import java.net.URI;

public class UsersClientFactory {

    private static final String REST = "/rest";
    private static final String SOAP = "/soap";

    public static Users get(URI serverURI) {
        var uriString = serverURI.toString();

        if (uriString.endsWith(REST))
//            return new RestUsersClient(serverURI);
            return null;
        else if (uriString.endsWith(SOAP))
            return new SoapUsersClient(serverURI);
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}
