package sd2223.trab1.api.java;

import sd2223.trab1.api.Message;

import java.util.List;

public class FeedsImpl implements Feeds {
    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
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
