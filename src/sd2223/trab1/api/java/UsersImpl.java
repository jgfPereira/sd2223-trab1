package sd2223.trab1.api.java;

import sd2223.trab1.api.User;

import java.util.List;

public class UsersImpl implements Users {
    @Override
    public Result<String> createUser(User user) {
        return null;
    }

    @Override
    public Result<User> getUser(String name, String pwd) {
        return null;
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
