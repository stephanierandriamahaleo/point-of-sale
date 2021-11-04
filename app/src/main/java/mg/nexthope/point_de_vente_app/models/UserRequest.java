package mg.nexthope.point_de_vente_app.models;

public class UserRequest {
    private String login;
    private String password;
    private String role;

    public UserRequest(String email, String password, String role) {
        this.login = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return login;
    }

    public void setEmail(String email) {
        this.login = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
