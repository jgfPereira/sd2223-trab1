package sd2223.trab1.clients;

import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.clients.soap.SoapFeedsClient;

import java.net.URI;

public class FeedsClientFactory {

    private static final String REST = "/rest";
    private static final String SOAP = "/soap";

    public static Feeds get(URI serverURI) {
        var uriString = serverURI.toString();
        if (uriString.endsWith(REST))
//            TODO uncomment when implement interoperable soap and rest servers
//            return new RestFeedsClient(serverURI);
            return null;
        else if (uriString.endsWith(SOAP))
            return new SoapFeedsClient(serverURI);
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}
