package finalproject.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import finalproject.models.User;
import finalproject.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class UserDetailsImpl implements UserDetails {


    private static final long serialVersoinUID = 1L;
    private long id;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(long id, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public UserDetailsImpl(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public UserDetailsImpl(long id, String email, String password, List<GrantedAuthority> authorities) {

    }

    public UserDetailsImpl(User user) {

    }

    //    public static UserDetailsImpl build(User user) {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        return new UserDetailsImpl(
//                user.getId(),
//                user.getEmail(),
//                user.getPassword(),
//                authorities);
//    }
public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("USER"));

    return new UserDetailsImpl(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            authorities);
}



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}