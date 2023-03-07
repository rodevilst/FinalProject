package finalproject.pojo;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private int id;
    private String email;

    public JwtResponse(String token, int id, String email) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
