package sd2223.trab1.clients;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.api.rest.UsersService;

import java.net.URI;
import java.util.List;

public class RestFeedsClient extends RestClient implements FeedsService {

    private static final String SERVICE_NAME_FMT = "%s:feeds";
    final WebTarget target;
    private final String domain;

    RestFeedsClient(String domain) {
        super();
        this.domain = domain;
        this.serverURI = this.searchServer(domain);
        target = client.target(serverURI).path(FeedsService.PATH);
    }

    private URI searchServer(String domain) {
        return this.discovery.knownUrisOf(String.format(SERVICE_NAME_FMT, domain), 1)[0];
    }

    public long clt_postMessage(String user, String pwd, Message msg) {
        Response r = target.path(user).queryParam(UsersService.PWD, pwd).request().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(msg, MediaType.APPLICATION_JSON));
        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity()) {
            long msgId = r.readEntity(long.class);
            System.out.println("Success, published msg with id: " + msgId);
            return msgId;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
            return -1;
        }
    }

    public Void clt_subUser(String user, String userSub, String pwd) {
        Response r = target.path("/sub")
                .path(user)
                .path(userSub)
                .queryParam(UsersService.PWD, pwd).request()
                .post(null);
        if (r.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("Success, " + user + " has subscribed to " + userSub);
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
        }
        return null;
    }

    public Void clt_unsubscribeUser(String user, String userSub, String pwd) {
        Response r = target.path("/sub")
                .path(user)
                .path(userSub)
                .queryParam(UsersService.PWD, pwd).request()
                .delete();
        if (r.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("Success, " + user + " has unsubscribed to " + userSub);
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
        }
        return null;
    }

    public List<String> clt_listSubs(String user) {
        Response r = target.path("/sub/list")
                .path(user)
                .request()
                .get();
        if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity()) {
            List<String> userSubs = r.readEntity(List.class);
            System.out.println("Success, list of subscribers: " + userSubs);
            return userSubs;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
        }
        return null;
    }

    public void clt_removeFromPersonalFeed(String user, long mid, String pwd) {

    }

    public Message clt_getMessage(String user, long mid) {
        return null;
    }

    public List<Message> clt_getMessages(String user, long time) {
        return null;
    }


    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.reTry(() -> clt_postMessage(user, pwd, msg));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        super.reTry(() -> clt_subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        super.reTry(() -> clt_unsubscribeUser(user, userSub, pwd));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {

    }

    @Override
    public Message getMessage(String user, long mid) {
        return null;
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return null;
    }

    @Override
    public List<String> listSubs(String user) {
        return super.reTry(() -> clt_listSubs(user));
    }
}
