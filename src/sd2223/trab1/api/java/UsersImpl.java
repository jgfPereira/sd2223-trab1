package sd2223.trab1.api.java;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.User;

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
        return null;
    }

    @Override
    public Result<User> deleteUser(String name, String pwd) {
        return null;
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return null;
    }

    @Override
    public Result<Void> verifyPassword(String name, String pwd) {
        return null;
    }
}
