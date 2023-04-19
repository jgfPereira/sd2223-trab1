package sd2223.trab1.server.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class UsersResource implements UsersService {

    private static final Logger Log = Logger.getLogger(UsersResource.class.getName());
    private final Map<String, User> users = new HashMap<>();

    public UsersResource() {
    }

    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);
        // Check if user data is valid
        if (user.getName() == null || user.getPwd() == null || user.getDisplayName() == null
                || user.getDomain() == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        // Insert user, checking if name already exists
        if (users.putIfAbsent(user.getName(), user) != null) {
            Log.info("User already exists");
            throw new WebApplicationException(Status.CONFLICT);
        }
        Log.fine("User created " + user.getName());
        return user.getName() + "@" + user.getDomain();
    }

    @Override
    public User getUser(String name, String pwd) {
        Log.info("getUser : user = " + name + "; pwd = " + pwd);
        // Check if user is valid
        if (name == null || pwd == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        User user = users.get(name);
        // Check if user exists
        if (user == null) {
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        // Check if the password is correct
        if (!user.getPwd().equals(pwd)) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Status.FORBIDDEN);
        }
        return user;
    }

    @Override
    public User updateUser(String name, String password, User user) {
        Log.info("updateUser : name = " + name + "; pwd = " + password + " ; user = " + user);
        // Check if user is valid
        if (name == null || password == null || user == null) {
            Log.info("User data invalid");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        // Check if userTemp exists
        var userTemp = users.get(name);
        if (userTemp == null) {
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        // Check if the password is correct
        if (!userTemp.getPwd().equals(password)) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Status.FORBIDDEN);
        }
        // fields not to update are passed as 'null' on cli
        this.handleNullFields(user, userTemp);
        this.users.put(name, user);
        return user;
    }

    private void handleNullFields(User user, User userTemp) {
        if (user.getDomain().equals("null")) {
            user.setDomain(userTemp.getDomain());
        }
        if (user.getDisplayName().equals("null")) {
            user.setDisplayName(userTemp.getDisplayName());
        }
        if (user.getPwd().equals("null")) {
            user.setPwd(userTemp.getPwd());
        }
    }

    @Override
    public User deleteUser(String name, String password) {
        Log.info("deleteUser : user = " + name + "; pwd = " + password);
        // Check if user is valid
        if (name == null || password == null) {
            Log.info("Invalid data");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        // Check if user exists
        var user = users.get(name);
        if (user == null) {
            Log.info("User does not exist");
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        // Check if the password is correct
        if (!user.getPwd().equals(password)) {
            Log.info("Password is incorrect");
            throw new WebApplicationException(Status.FORBIDDEN);
        }
        this.users.remove(name);
        return user;
    }

    @Override
    public List<User> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);
        if (pattern == null) {
            Log.info("Invalid Pattern");
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        List<User> res = new ArrayList<>();
        for (User u : this.users.values()) {
            if (u.getDisplayName().toLowerCase().contains(pattern.toLowerCase())) {
                User tmp = new User(u.getName(), "", u.getDomain(), u.getDisplayName());
                res.add(tmp);
            }
        }
        return res;
    }

    @Override
    public Response resp_getUser(String name, String pwd) {
        return null;
    }
}
