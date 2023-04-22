package sd2223.trab1.clients.soap;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.soap.FeedsService;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.List;

public class SoapFeedsClient extends SoapClient implements Feeds {

    private static final String SERVICE_NAME_FMT = "%s:feeds";
    private FeedsService stub;
    private String domain;

    public SoapFeedsClient(URI uri) {
        super(uri);
    }

    public SoapFeedsClient(String domain) {
        super();
        this.domain = domain;
        this.uri = this.searchServer(domain);
    }

    private URI searchServer(String domain) {
        return this.discovery.knownUrisOf(String.format(SERVICE_NAME_FMT, domain), 1)[0];
    }

    synchronized private FeedsService stub() {
        if (stub == null) {
            QName QNAME = new QName(FeedsService.NAMESPACE, FeedsService.NAME);
            Service service = Service.create(toURL(super.uri + WSDL), QNAME);
            this.stub = service.getPort(FeedsService.class);
            super.setTimeouts((BindingProvider) stub);
        }
        return stub;
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
//        return super.reTry(() -> super.toJavaResult(() -> stub().createUser(user)));
        return null;
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        return null;
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        return null;
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        return null;
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        return null;
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        return null;
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return null;
    }

    @Override
    public Result<List<Message>> getUserOnlyMessages(String user) {
        return null;
    }
}
