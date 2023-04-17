package sd2223.trab1.api.discovery;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServiceInfo {

    private final List<URIEntry> uris;

    public ServiceInfo() {
        this.uris = new ArrayList<>();
    }

    public void addEntry(URIEntry e) {
        this.uris.add(e);
    }

    public List<URIEntry> getAllEntrys() {
        return this.uris;
    }

    public List<URI> getAllUris() {
        List<URI> res = new ArrayList<>();
        for (URIEntry e : this.uris) {
            res.add(e.getUri());
        }
        return res;
    }

    public URIEntry getEntry(URI uri) {
        for (URIEntry e : this.uris) {
            if (e.getUri().equals(uri)) {
                return e;
            }
        }
        return null;
    }

    public String[] getHostAndPort() throws MalformedURLException {
        URL url = this.uris.get(0).getUri().toURL();
        String uri = url.getHost();
        String port = String.valueOf(url.getPort());
        return new String[]{uri, port};
    }

    public void printAllUris(String serviceName) {
        System.out.println("All known URI's of + " + serviceName + ":");
        for (URIEntry e : this.uris) {
            System.out.println(e.getUri());
        }
    }
}
