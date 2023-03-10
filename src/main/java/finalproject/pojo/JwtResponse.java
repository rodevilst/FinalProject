package finalproject.pojo;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private long id;
    private String email;

    public JwtResponse(String token, long id, String email) {
        this.token = token;
        this.id = id;
        this.email = email;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
