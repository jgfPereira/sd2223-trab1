package sd2223.trab1.server.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.clients.RestUsersClient;

import java.util.*;
import java.util.logging.Logger;

@Singleton
public class FeedsResource implements FeedsService {

    private static final Logger Log = Logger.getLogger(FeedsResource.class.getName());
    private final Map<String, List<Message>> feeds = new HashMap<>();
    private final Map<String, List<String>> subs = new HashMap<>();

    public FeedsResource() {
    }


    @Override
    public long postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : " + user + " " + msg);
        if (user == null || pwd == null || msg == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String uname = user.split("@")[0];
        final String domain = user.split("@")[1];
        var respGetUser = new RestUsersClient(domain).resp_getUser(uname, pwd);
        if (respGetUser.getStatus() == Response.Status.OK.getStatusCode() && respGetUser.hasEntity()) {
            long msgId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
            msg.setId(msgId);
            this.feeds.putIfAbsent(uname, new ArrayList<>());
            this.feeds.get(uname).add(msg);
            return msgId;
        } else if (respGetUser.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUser.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        Log.info("Bad Request");
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        Log.info("subUser : " + user + " " + userSub);
        if (user == null || userSub == null || pwd == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String subscriber = user.split("@")[0];
        final String subscriberDomain = user.split("@")[1];
        final String subscribed = userSub.split("@")[0];
        final String subscribedDomain = userSub.split("@")[1];
        var respGetSubscriber = new RestUsersClient(subscriberDomain).resp_getUser(subscriber, pwd);
        var respGetSubscribed = new RestUsersClient(subscribedDomain).resp_internal_getUser(subscribed);

        if (respGetSubscriber.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetSubscriber.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else if (respGetSubscriber.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetSubscribed.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetSubscribed.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetSubscriber.getStatus() == Response.Status.OK.getStatusCode() && respGetSubscriber.hasEntity()
                && respGetSubscribed.getStatus() == Response.Status.OK.getStatusCode() && respGetSubscribed.hasEntity()) {
            this.subs.putIfAbsent(subscribed, new ArrayList<>());
            this.subs.get(subscribed).add(subscriber);
        }
    }


    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {

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
        return null;
    }
}
