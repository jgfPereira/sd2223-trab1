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
        Log.info("subUser : " + user + " " + userSub + " " + pwd);
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
            List<String> listSubs = this.subs.get(subscribed);
            if (!listSubs.contains(subscriber)) {
                Log.info("Success, " + subscriber + " has subscribed to " + subscribed);
                listSubs.add(subscriber);
            }
        }
    }


    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        Log.info("unsubscribeUser : " + user + " " + userSub + " " + pwd);
        if (user == null || userSub == null || pwd == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String unsubscriber = user.split("@")[0];
        final String unsubscriberDomain = user.split("@")[1];
        final String subscribed = userSub.split("@")[0];
        final String subscribedDomain = userSub.split("@")[1];
        var respGetUnsubscriber = new RestUsersClient(unsubscriberDomain).resp_getUser(unsubscriber, pwd);
        var respGetSubscribed = new RestUsersClient(subscribedDomain).resp_internal_getUser(subscribed);

        if (respGetUnsubscriber.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUnsubscriber.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else if (respGetUnsubscriber.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
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
        if (respGetUnsubscriber.getStatus() == Response.Status.OK.getStatusCode() && respGetUnsubscriber.hasEntity()
                && respGetSubscribed.getStatus() == Response.Status.OK.getStatusCode() && respGetSubscribed.hasEntity()) {
            List<String> listSubs = this.subs.get(subscribed);
            if (listSubs != null) {
                if (listSubs.remove(unsubscriber)) {
                    Log.info("Success, " + unsubscriber + " has unsubscribed to " + subscribed);
                } else {
                    Log.info("Success, " + unsubscriber + " was not subscribed to " + subscribed);
                }
            }
        }
    }

    @Override
    public List<String> listSubs(String user) {
        Log.info("listSubs : " + user);
        if (user == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String listUser = user.split("@")[0];
        final String listUserDomain = user.split("@")[1];
        var respGetUser = new RestUsersClient(listUserDomain).resp_internal_getUser(listUser);
        if (respGetUser.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUser.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetUser.getStatus() == Response.Status.OK.getStatusCode() && respGetUser.hasEntity()) {
            List<String> resSubs = this.subs.get(listUser);
            if (resSubs == null) {
                resSubs = new ArrayList<>();
            }
            return resSubs;
        }
        return null;
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        Log.info("getMessages : " + user + ", time newer then " + time);
        if (user == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String feedUser = user.split("@")[0];
        final String feedUserDomain = user.split("@")[1];
        var respGetUser = new RestUsersClient(feedUserDomain).resp_internal_getUser(feedUser);
        if (respGetUser.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUser.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetUser.getStatus() == Response.Status.OK.getStatusCode() && respGetUser.hasEntity()) {
            List<Message> allFeed = new ArrayList<>();
            List<Message> userMessages = this.feeds.getOrDefault(feedUser, new ArrayList<>());
            this.addMessagesNewer(allFeed, userMessages, time);

            List<String> userSubs = this.subs.getOrDefault(feedUser, new ArrayList<>());
            for (String subscriber : userSubs) {
                List<Message> subscriberMessages = this.feeds.getOrDefault(subscriber, new ArrayList<>());
                this.addMessagesNewer(allFeed, subscriberMessages, time);
            }
            return allFeed;
        }
        return null;
    }

    private void addMessagesNewer(List<Message> allFeed, List<Message> other, long time) {
        for (Message m : other) {
            if (m.getCreationTime() > time) {
                allFeed.add(m);
            }
        }
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {

    }

    @Override
    public Message getMessage(String user, long mid) {
        return null;
    }
}
