package sd2223.trab1.clients.soap;

public class GetUserClient {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("usage: name pwd domain");
            System.exit(0);
        }
        var name = args[0];
        var pwd = args[1];
        var domain = args[2];
        var soapClient = new SoapUsersClient(domain);
        var res = soapClient.getUser(name, pwd);
        System.out.println(res);
    }

}
