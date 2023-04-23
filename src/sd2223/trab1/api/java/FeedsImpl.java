package sd2223.trab1.api.java;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sd2223.trab1.api.Message;
import sd2223.trab1.clients.soap.SoapFeedsClient;
import sd2223.trab1.clients.soap.SoapUsersClient;

import java.util.*;
import java.util.logging.Logger;

public class FeedsImpl implements Feeds {

    private static final Logger Log = Logger.getLogger(FeedsImpl.class.getName());
    private final Map<String, List<Message>> feeds = new HashMap<>();
    private final Map<String, List<String>> subs = new HashMap<>();

    public FeedsImpl() {
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : " + user + " " + msg);
        if (user == null || pwd == null || msg == null || !(user.split("@")[1].equals(msg.getDomain()))) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        final String uname = user.split("@")[0];
        final String domain = user.split("@")[1];
        var getUser = new SoapUsersClient(domain).getUser(uname, pwd);
        if (getUser.isOK()) {
            long msgId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
            msg.setId(msgId);
            synchronized (feeds) {
                this.feeds.putIfAbsent(user, new ArrayList<>());
                this.feeds.get(user).add(msg);
            }
            return new OkResult<>(msgId);
        } else {
            Log.info(getUser.toString());
            switch (getUser.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        }
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        Log.info("subUser : " + user + " " + userSub + " " + pwd);
        if (user == null || userSub == null || pwd == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        final String followed = user.split("@")[0];
        final String followedDomain = user.split("@")[1];
        final String follower = userSub.split("@")[0];
        final String followerDomain = userSub.split("@")[1];
        var getFollowed = new SoapUsersClient(followedDomain).getUser(followed, pwd);
        var getFollower = new SoapUsersClient(followerDomain).internal_getUser(follower);
        if (!getFollowed.isOK()) {
            Log.info(getFollowed.toString());
            switch (getFollowed.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        }
        if (!getFollower.isOK()) {
            Log.info(getFollower.toString());
            switch (getFollower.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        }
        if (getFollowed.isOK() && getFollower.isOK()) {
            synchronized (subs) {
                this.subs.putIfAbsent(user, new ArrayList<>());
                List<String> listSubs = this.subs.get(user);
                if (!listSubs.contains(userSub)) {
                    Log.info("Success, " + follower + " has subscribed to " + followed);
                    listSubs.add(userSub);
                }
            }
        }
        return null;
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        Log.info("unsubscribeUser : " + user + " " + userSub + " " + pwd);
        if (user == null || userSub == null || pwd == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        final String unfollowed = user.split("@")[0];
        final String unfollowedDomain = user.split("@")[1];
        final String unfollower = userSub.split("@")[0];
        final String unfollowerDomain = userSub.split("@")[1];
        var getUnfollowed = new SoapUsersClient(unfollowedDomain).getUser(unfollowed, pwd);
        var getUnfollower = new SoapUsersClient(unfollowerDomain).internal_getUser(unfollower);
        if (!getUnfollowed.isOK()) {
            Log.info(getUnfollowed.toString());
            switch (getUnfollowed.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        }
        if (!getUnfollower.isOK()) {
            Log.info(getUnfollower.toString());
            switch (getUnfollower.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        }
        if (getUnfollowed.isOK() && getUnfollower.isOK()) {
            synchronized (subs) {
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
        return null;
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        Log.info("listSubs : " + user);
        if (user == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        final String listUser = user.split("@")[0];
        final String listUserDomain = user.split("@")[1];
        var getUser = new SoapUsersClient(listUserDomain).internal_getUser(listUser);
        if (!getUser.isOK()) {
            Log.info(getUser.toString());
            switch (getUser.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        } else {
            synchronized (subs) {
                List<String> resSubs = this.subs.get(user);
                if (resSubs == null) {
                    resSubs = new ArrayList<>();
                }
                return new OkResult<>(resSubs);
            }
        }
    }

    @Override
    public Result<List<Message>> getUserOnlyMessages(String user) {
        Log.info("getUserOnlyMessages : " + user);
        if (user == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        final String uname = user.split("@")[0];
        final String userDomain = user.split("@")[1];
        var getUser = new SoapUsersClient(userDomain).internal_getUser(uname);
        if (!getUser.isOK()) {
            Log.info(getUser.toString());
            switch (getUser.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        } else {
            synchronized (feeds) {
                return new OkResult<>(this.feeds.getOrDefault(user, new ArrayList<>()));
            }
        }
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        Log.info("getMessages : " + user + ", time newer then " + time);
        if (user == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        final String feedUser = user.split("@")[0];
        final String feedUserDomain = user.split("@")[1];
        var getUser = new SoapUsersClient(feedUserDomain).internal_getUser(feedUser);
        if (!getUser.isOK()) {
            Log.info(getUser.toString());
            switch (getUser.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        } else {
            List<Message> allFeed = new ArrayList<>();
            var userOnlyMsgs = new SoapFeedsClient(feedUserDomain).getUserOnlyMessages(user);
            if (userOnlyMsgs.isOK()) {
                Result<List<Message>> userOnlyMsgList = this.convertToResultListMsg(userOnlyMsgs);
                this.addMessagesNewer(allFeed, userOnlyMsgList, time);
            }
            var userSubs = new SoapFeedsClient(feedUserDomain).listSubs(user);
            Result<List<String>> userSubsList = null;
            if (userSubs.isOK()) {
                userSubsList = this.convertToResultListStr(userSubs);
            }
            for (String subscriber : userSubsList.value()) {
                var subscriberOnlyMsgs = new SoapFeedsClient(subscriber.split("@")[1]).getUserOnlyMessages(subscriber);
                if (subscriberOnlyMsgs != null) {
                    Result<List<Message>> subOnlyMsgList = this.convertToResultListMsg(subscriberOnlyMsgs);
                    this.addMessagesNewer(allFeed, subOnlyMsgList, time);
                }
            }
            return new OkResult<>(allFeed);
        }
    }

    private Result<List<Message>> convertToResultListMsg(Result<List<Message>> rlmsg) {
        ObjectMapper mapper = new ObjectMapper();
        Result<List<Message>> converted = mapper.convertValue(rlmsg, new TypeReference<Result<List<Message>>>() {
        });
        return converted;
    }

    private Result<List<String>> convertToResultListStr(Result<List<String>> rlsub) {
        ObjectMapper mapper = new ObjectMapper();
        Result<List<String>> converted = mapper.convertValue(rlsub, new TypeReference<Result<List<String>>>() {
        });
        return converted;
    }

    private void addMessagesNewer(List<Message> allFeed, Result<List<Message>> other, long time) {
        for (Message m : other.value()) {
            if (m.getCreationTime() > time) {
                allFeed.add(m);
            }
        }
    }

    private void addAllMessages(List<Message> allMsgs, Result<List<Message>> other) {
        for (Message m : other.value()) {
            allMsgs.add(m);
        }
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        Log.info("getMessage : " + mid + ", of user " + user);
        if (user == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        final String msgUser = user.split("@")[0];
        final String msgUserDomain = user.split("@")[1];
        var getUser = new SoapUsersClient(msgUserDomain).internal_getUser(msgUser);
        if (!getUser.isOK()) {
            Log.info(getUser.toString());
            switch (getUser.error()) {
                case BAD_REQUEST:
                    return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
                case NOT_FOUND:
                    return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
                case FORBIDDEN:
                    return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
                default:
                    return new ErrorResult<>(Result.ErrorCode.INTERNAL_ERROR);
            }
        } else {
            List<Message> allFeed = new ArrayList<>();
            var userOnlyMsgs = new SoapFeedsClient(msgUserDomain).getUserOnlyMessages(user);
            if (userOnlyMsgs.isOK()) {
                Result<List<Message>> userOnlyMsgList = this.convertToResultListMsg(userOnlyMsgs);
                this.addAllMessages(allFeed, userOnlyMsgList);
            }
            var userSubs = new SoapFeedsClient(msgUserDomain).listSubs(user);
            Result<List<String>> userSubsList = null;
            if (userSubs.isOK()) {
                userSubsList = this.convertToResultListStr(userSubs);
            }
            for (String subscriber : userSubsList.value()) {
                var subscriberOnlyMsgs = new SoapFeedsClient(subscriber.split("@")[1]).getUserOnlyMessages(subscriber);
                if (subscriberOnlyMsgs != null) {
                    Result<List<Message>> subOnlyMsgList = this.convertToResultListMsg(subscriberOnlyMsgs);
                    this.addAllMessages(allFeed, subOnlyMsgList);
                }
            }
            Message m = this.getMessageById(allFeed, mid);
            if (m == null) {
                Log.info("Message does not exist");
                return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
            }
            return new OkResult<>(m);
        }
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
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        return null;
    }

}
