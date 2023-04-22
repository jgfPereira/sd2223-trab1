package sd2223.trab1.server.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.clients.RestFeedsClient;
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
        if (user == null || pwd == null || msg == null || !(user.split("@")[1].equals(msg.getDomain()))) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String uname = user.split("@")[0];
        final String domain = user.split("@")[1];
        var respGetUser = new RestUsersClient(domain).resp_getUser(uname, pwd);
        if (respGetUser.getStatus() == Response.Status.OK.getStatusCode() && respGetUser.hasEntity()) {
            long msgId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
            msg.setId(msgId);
            this.feeds.putIfAbsent(user, new ArrayList<>());
            this.feeds.get(user).add(msg);
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
        final String followed = user.split("@")[0];
        final String followedDomain = user.split("@")[1];
        final String follower = userSub.split("@")[0];
        final String followerDomain = userSub.split("@")[1];
        var respGetFollowed = new RestUsersClient(followedDomain).resp_getUser(followed, pwd);
        var respGetFollower = new RestUsersClient(followerDomain).resp_internal_getUser(follower);

        if (respGetFollowed.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetFollowed.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else if (respGetFollowed.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetFollower.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetFollower.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetFollowed.getStatus() == Response.Status.OK.getStatusCode() && respGetFollowed.hasEntity()
                && respGetFollower.getStatus() == Response.Status.OK.getStatusCode() && respGetFollower.hasEntity()) {
            this.subs.putIfAbsent(user, new ArrayList<>());
            List<String> listSubs = this.subs.get(user);
            if (!listSubs.contains(userSub)) {
                Log.info("Success, " + follower + " has subscribed to " + followed);
                listSubs.add(userSub);
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
        final String unfollowed = user.split("@")[0];
        final String unfollowedDomain = user.split("@")[1];
        final String unfollower = userSub.split("@")[0];
        final String unfollowerDomain = userSub.split("@")[1];
        var respGetUnfollowed = new RestUsersClient(unfollowedDomain).resp_getUser(unfollowed, pwd);
        var respGetUnfollower = new RestUsersClient(unfollowerDomain).resp_internal_getUser(unfollower);

        if (respGetUnfollowed.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUnfollowed.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else if (respGetUnfollowed.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetUnfollower.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUnfollower.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetUnfollowed.getStatus() == Response.Status.OK.getStatusCode() && respGetUnfollowed.hasEntity()
                && respGetUnfollower.getStatus() == Response.Status.OK.getStatusCode() && respGetUnfollower.hasEntity()) {
            List<String> listSubs = this.subs.get(user);
            if (listSubs != null) {
                if (listSubs.remove(userSub)) {
                    Log.info("Success, " + unfollower + " has unsubscribed to " + unfollowed);
                } else {
                    Log.info("Success, " + unfollower + " was not subscribed to " + unfollowed);
                }
            } else {
                Log.info("Success, " + unfollower + " was not subscribed to " + unfollowed);
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
            List<String> resSubs = this.subs.get(user);
            if (resSubs == null) {
                resSubs = new ArrayList<>();
            }
            return resSubs;
        }
        return null;
    }

    @Override
    public List<Message> getUserOnlyMessages(String user) {
        Log.info("getUserOnlyMessages : " + user);
        if (user == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String uname = user.split("@")[0];
        final String userDomain = user.split("@")[1];
        var respGetUser = new RestUsersClient(userDomain).resp_internal_getUser(uname);
        if (respGetUser.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUser.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetUser.getStatus() == Response.Status.OK.getStatusCode() && respGetUser.hasEntity()) {
            return this.feeds.getOrDefault(user, new ArrayList<>());
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
            var userOnlyMsgs = new RestFeedsClient(feedUserDomain).getUserOnlyMessages(user);
            if (userOnlyMsgs != null) {
                List<Message> userOnlyMsgList = this.convertToList(userOnlyMsgs);
                this.addMessagesNewer(allFeed, userOnlyMsgList, time);
            }
            var userSubsList = new RestFeedsClient(feedUserDomain).listSubs(user);
            for (String subscriber : userSubsList) {
                var subscriberOnlyMsgs = new RestFeedsClient(subscriber.split("@")[1]).getUserOnlyMessages(subscriber);
                if (subscriberOnlyMsgs != null) {
                    List<Message> subOnlyMsgList = this.convertToList(subscriberOnlyMsgs);
                    this.addMessagesNewer(allFeed, subOnlyMsgList, time);
                }
            }
            return allFeed;
        }
        return null;
    }

    private List<Message> convertToList(List<Message> lmsg) {
        ObjectMapper mapper = new ObjectMapper();
        List<Message> converted = mapper.convertValue(lmsg, new TypeReference<List<Message>>() {
        });
        return converted;
    }

    private void addMessagesNewer(List<Message> allFeed, List<Message> other, long time) {
        for (Message m : other) {
            if (m.getCreationTime() > time) {
                allFeed.add(m);
            }
        }
    }

    private void addAllMessages(List<Message> allMsgs, List<Message> other) {
        for (Message m : other) {
            allMsgs.add(m);
        }
    }

    @Override
    public Message getMessage(String user, long mid) {
        Log.info("getMessage : " + mid + ", of user " + user);
        if (user == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String msgUser = user.split("@")[0];
        final String msgUserDomain = user.split("@")[1];
        var respGetUser = new RestUsersClient(msgUserDomain).resp_internal_getUser(msgUser);
        if (respGetUser.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUser.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetUser.getStatus() == Response.Status.OK.getStatusCode() && respGetUser.hasEntity()) {
            List<Message> lmsg = new ArrayList<>();
            var userOnlyMsgs = new RestFeedsClient(msgUserDomain).getUserOnlyMessages(user);
            if (userOnlyMsgs != null) {
                List<Message> userOnlyMsgList = this.convertToList(userOnlyMsgs);
                this.addAllMessages(lmsg, userOnlyMsgList);
            }
            var userSubsList = new RestFeedsClient(msgUserDomain).listSubs(user);
            for (String subscriber : userSubsList) {
                var subscriberOnlyMsgs = new RestFeedsClient(subscriber.split("@")[1]).getUserOnlyMessages(subscriber);
                if (subscriberOnlyMsgs != null) {
                    List<Message> subOnlyMsgList = this.convertToList(subscriberOnlyMsgs);
                    this.addAllMessages(lmsg, subOnlyMsgList);
                }
            }
            Message m = this.getMessageById(lmsg, mid);
            if (m == null) {
                Log.info("Message does not exist");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            return m;
        }
        Log.info("Some error occurred");
        return null;
    }

    private Message getMessageById(List<Message> lmsg, long mid) {
        for (Message m : lmsg) {
            if (m.getId() == mid) {
                return m;
            }
        }
        return null;
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        Log.info("removeFromPersonalFeed : " + mid + ", of user " + user);
        if (user == null || pwd == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final String uname = user.split("@")[0];
        final String userDomain = user.split("@")[1];
        var respGetUser = new RestUsersClient(userDomain).resp_getUser(uname, pwd);
        if (respGetUser.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            Log.info("User does not exist");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (respGetUser.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else if (respGetUser.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            Log.info("User data invalid");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (respGetUser.getStatus() == Response.Status.OK.getStatusCode() && respGetUser.hasEntity()) {
            List<Message> lmsg = this.feeds.getOrDefault(user, new ArrayList<>());
            Message m = this.getMessageById(lmsg, mid);
            if (m == null) {
                Log.info("Message does not exist");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            } else {
                lmsg.remove(m);
                Log.info("Message removed successfully");
            }
        }
    }
}
