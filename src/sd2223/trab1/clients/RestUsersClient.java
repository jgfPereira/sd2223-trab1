package sd2223.trab1.clients;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;

import java.net.URI;
import java.util.List;

public class RestUsersClient extends RestClient implements UsersService {

    private static final String SERVICE_NAME_FMT = "%s:users";
    final WebTarget target;
    private final String domain;

    public RestUsersClient(String domain) {
        super();
        this.domain = domain;
        this.serverURI = this.searchServer(domain);
        target = client.target(serverURI).path(UsersService.PATH);
    }

    private URI searchServer(String domain) {
        return this.discovery.knownUrisOf(String.format(SERVICE_NAME_FMT, domain), 1)[0];
    }

    private String clt_createUser(User user) {
        Response r = target.request().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            String u = r.readEntity(String.class);
            System.out.println("Success, created user with id@domain: " + u);
            return u;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    private Response clt_resp_getUser(String name, String pwd) {
        return target.path(name).queryParam(UsersService.PWD, pwd).request().accept(MediaType.APPLICATION_JSON)
                .get();
    }

    private Response clt_resp_internal_getUser(String name) {
        return target.path("/internal").queryParam(UsersService.NAME, name).request().accept(MediaType.APPLICATION_JSON)
                .get();
    }

    private User clt_internal_getUser(String name) {
        Response r = target.path("/internal").queryParam(UsersService.NAME, name).request().accept(MediaType.APPLICATION_JSON)
                .get();
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            User userEntity = r.readEntity(User.class);
            System.out.println("Success, fetched user with id: " + userEntity.getName());
            return userEntity;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
            return null;
        }
    }


    private User clt_getUser(String name, String pwd) {
        // in the end change to post because password will be visible on url
        Response r = target.path(name).queryParam(UsersService.PWD, pwd).request().accept(MediaType.APPLICATION_JSON)
                .get();
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            User userEntity = r.readEntity(User.class);
            System.out.println("Success, fetched user with id: " + userEntity.getName());
            return userEntity;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    private User clt_updateUser(String name, String pwd, User user) {
        Response r = target.path(name).queryParam(UsersService.PWD, pwd).request().accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            User userEntity = r.readEntity(User.class);
            System.out.println("Success, updated user with id: " + userEntity.getName());
            return userEntity;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    private User clt_deleteUser(String name, String password) {
        Response r = target.path(name).queryParam(UsersService.PWD, password).request()
                .accept(MediaType.APPLICATION_JSON).delete();
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            User userEntity = r.readEntity(User.class);
            System.out.println("Success, deleted user with id: " + userEntity.getName());
            return userEntity;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    private List<User> clt_searchUsers(String pattern) {
        Response r = target.path("/").queryParam(UsersService.QUERY, pattern).request()
                .accept(MediaType.APPLICATION_JSON).get();
        if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
            var users = r.readEntity(new GenericType<List<User>>() {
            });
            System.out.println("Success: (" + users.size() + " users)");
            users.stream().forEach(u -> System.out.println(u));
            return users;
        } else {
            System.out.println("Error, HTTP error status: " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase());
            return null;
        }
    }

    @Override
    public User deleteUser(String name, String pwd) {
        return super.reTry(() -> clt_deleteUser(name, pwd));
    }

    @Override
    public String createUser(User user) {
        return super.reTry(() -> clt_createUser(user));
    }

    @Override
    public User getUser(String name, String pwd) {
        return super.reTry(() -> clt_getUser(name, pwd));
    }

    @Override
    public Response resp_getUser(String name, String pwd) {
        return super.reTry(() -> clt_resp_getUser(name, pwd));
    }

    @Override
    public User internal_getUser(String name) {
        return super.reTry(() -> clt_internal_getUser(name));
    }

    @Override
    public Response resp_internal_getUser(String name) {
        return super.reTry(() -> clt_resp_internal_getUser(name));
    }

    @Override
    public User updateUser(String name, String pwd, User user) {
        return super.reTry(() -> clt_updateUser(name, pwd, user));
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return super.reTry(() -> clt_searchUsers(pattern));
    }
}
