package sd2223.trab1.clients.soap;


import sd2223.trab1.api.User;


public class CreateUserClient {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("usage: name pwd domain displayName");
            System.exit(0);
        }
        var name = args[0];
        var pwd = args[1];
        var domain = args[2];
        var displayName = args[3];
        var soapClient = new SoapUsersClient(domain);
        var res = soapClient.createUser(new User(name, pwd, domain, displayName));
        System.out.println(res);
    }
}