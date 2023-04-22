package sd2223.trab1.api.java;

import sd2223.trab1.api.Message;
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
    public Result<List<String>> listSubs(String user) {
        return null;
    }

    @Override
    public Result<List<Message>> getUserOnlyMessages(String user) {
        return null;
    }
}
