package sd2223.trab1.api.java;

import sd2223.trab1.api.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UsersImpl implements Users {

    private static final Logger Log = Logger.getLogger(UsersImpl.class.getName());
    private final Map<String, User> users = new HashMap<>();

    public UsersImpl() {
    }

    @Override
    public Result<String> createUser(User user) {
        Log.info("createUser : " + user);
        // Check if user data is valid
        if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null
                || user.getDomain() == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        synchronized (users) {
            // Insert user, checking if name already exists
            if (users.putIfAbsent(user.getName(), user) != null) {
                Log.info("User already exists");
                return new ErrorResult<>(Result.ErrorCode.CONFLICT);
            }
        }
        Log.fine("User created " + user.getName());
        return new OkResult<>(user.getName() + "@" + user.getDomain());
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);
        // Check if user is valid
        if (name == null || pwd == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        synchronized (users) {
            User user = users.get(name);
            // Check if user exists
            if (user == null) {
                Log.info("User does not exist");
                return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
            }
            // Check if the password is correct
            if (!user.getPwd().equals(pwd)) {
                Log.info("Password is incorrect");
                return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
            }
            return new OkResult<>(user);
        }
    }

    @Override
    public Result<User> updateUser(String name, String pwd, User user) {
        Log.info("updateUser : name = " + name + "; pwd = " + pwd + " ; user = " + user);
        // Check if user is valid
        if (name == null || pwd == null || user == null || (!name.equals(user.getName()))) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        synchronized (users) {
            // Check if userTemp exists
            var userTemp = users.get(name);
            if (userTemp == null) {
                Log.info("User does not exist");
                return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
            }
            // Check if the password is correct
            if (!userTemp.getPwd().equals(pwd)) {
                Log.info("Password is incorrect");
                return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
            }
            // fields not to update are passed as null
            this.handleNullFields(user, userTemp);
            this.users.put(name, user);
            return new OkResult<>(user);
        }
    }

    private void handleNullFields(User user, User userTemp) {
        if (user.getDomain() == null) {
            user.setDomain(userTemp.getDomain());
        }
        if (user.getDisplayName() == null) {
            user.setDisplayName(userTemp.getDisplayName());
        }
        if (user.getPwd() == null) {
            user.setPwd(userTemp.getPwd());
        }
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) {
        Log.info("deleteUser : user = " + name + "; pwd = " + pwd);
        // Check if user is valid
        if (name == null || pwd == null) {
            Log.info("Invalid data");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        synchronized (users) {
            // Check if user exists
            var user = users.get(name);
            if (user == null) {
                Log.info("User does not exist");
                return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
            }
            // Check if the password is correct
            if (!user.getPwd().equals(pwd)) {
                Log.info("Password is incorrect");
                return new ErrorResult<>(Result.ErrorCode.FORBIDDEN);
            }
            this.users.remove(name);
            return new OkResult<>(user);
        }
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);
        if (pattern == null) {
            Log.info("Invalid Pattern");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        synchronized (users) {
            List<User> res = new ArrayList<>();
            for (User u : this.users.values()) {
                if (u.getName().toLowerCase().contains(pattern.toLowerCase())) {
                    User tmp = new User(u.getName(), "", u.getDomain(), u.getDisplayName());
                    res.add(tmp);
                }
            }
            return new OkResult<>(res);
        }
    }

    @Override
    public Result<Void> verifyPassword(String name, String pwd) {
        return null;
    }

    @Override
    public Result<User> internal_getUser(String name) {
        Log.info("getUser : user = " + name);
        // Check if user is valid
        if (name == null) {
            Log.info("User data invalid");
            return new ErrorResult<>(Result.ErrorCode.BAD_REQUEST);
        }
        synchronized (users) {
            User user = users.get(name);
            // Check if user exists
            if (user == null) {
                Log.info("User does not exist");
                return new ErrorResult<>(Result.ErrorCode.NOT_FOUND);
            }
            return new OkResult<>(user);
        }
    }
}
