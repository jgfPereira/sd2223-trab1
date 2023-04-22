package sd2223.trab1.clients.soap;

import sd2223.trab1.api.User;

import java.io.IOException;

public class UpdateUserClient {

    public static void main(String[] args) throws IOException {
        if (args.length != 6) {
            System.err.println("Usage: name oldpwd oldDomain displayName domain password");
            return;
        }
        String name = args[0];
        String oldpwd = args[1];
        String oldDomain = args[2];
        String displayName = args[3];
        String domain = args[4];
        String password = args[5];
        var updatedUser = new User(name, password, domain, displayName);
        var soapClient = new SoapUsersClient(oldDomain);
        var res = soapClient.updateUser(name, oldpwd, updatedUser);
        System.out.println(res);
    }
}
