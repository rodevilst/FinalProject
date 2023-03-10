package finalproject.controllers;

import finalproject.jwt.JwtUtils;
import finalproject.models.AccessToken;
import finalproject.models.Profile;
import finalproject.models.RefreshToken;
import finalproject.models.User;
import finalproject.pojo.JwtResponse;
import finalproject.pojo.LoginRequest;
import finalproject.pojo.MessageResponse;
import finalproject.pojo.SignUpRequest;
import finalproject.repository.AccessTokenRepository;
import finalproject.repository.ProfileRepository;
import finalproject.repository.RefreshTokenRepository;
import finalproject.repository.UserRepository;
import finalproject.service.UserDetailsImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    AccessTokenRepository accessTokenRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


    //    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error : Email is exist"));
//        } else if (signUpRequest.getEmail().isBlank()) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error : Email is empty"));
//        }
//
//        if (signUpRequest.getPassword().isBlank()) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error : Password is empty"));
//        } else if (signUpRequest.getPassword().length() < 8 || signUpRequest.getPassword().length() > 16) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Invalid password"));
//        }
//        User user = new User(signUpRequest.getEmail(),
//                passwordEncoder.encode(signUpRequest.getPassword()));
//        user.setCreated(new Date());
//        Profile profile = new Profile();
//        profile.setUser(user);
//        userRepository.save(user);
//        profileRepository.save(profile);
//
//        // gen access token
//        String accessToken = jwtUtils.generateAccessToken(user);
//        AccessToken accessTokenEntity = new AccessToken();
//        accessTokenEntity.setToken(accessToken);
//        accessTokenEntity.setUser(user);
//        accessTokenEntity.setCreatedAt(LocalDateTime.now());
//        accessTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(10));
//        accessTokenRepository.save(accessTokenEntity);
//
//        //gen refresh token
//        String refreshToken = jwtUtils.generateRefreshToken();
//        RefreshToken refreshTokenEntity = new RefreshToken();
//        refreshTokenEntity.setToken(refreshToken);
//        refreshTokenEntity.setUser(user);
//        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
//        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(20));
//        refreshTokenRepository.save(refreshTokenEntity);
//
//
//        return ResponseEntity.ok(new MessageResponse("User CREATED"));
//    }
@PostMapping("/signup")
public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error : Email is exist"));
    } else if (signUpRequest.getEmail().isBlank()) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error : Email is empty"));
    }
    if (signUpRequest.getPassword().isEmpty()) {

    } else {
        String encodedPassword = new BCryptPasswordEncoder().encode(signUpRequest.getPassword());
    }

    if (signUpRequest.getPassword().isBlank()) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error : Password is empty"));
    } else if (signUpRequest.getPassword().length() < 8 || signUpRequest.getPassword().length() > 16) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Invalid password"));
    }


    User user = new User(signUpRequest.getEmail(),
            passwordEncoder.encode(signUpRequest.getPassword()));
    user.setCreated(new Date());
    Profile profile = new Profile();
    profile.setUser(user);
    userRepository.save(user);
    profileRepository.save(profile);

    // gen access token
    String accessToken = jwtUtils.generateAccessToken(user);
    AccessToken accessTokenEntity = new AccessToken();
    accessTokenEntity.setToken(accessToken);
    accessTokenEntity.setUser(user);
    accessTokenEntity.setCreatedAt(LocalDateTime.now());
    accessTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(10));
    accessTokenRepository.save(accessTokenEntity);

    //gen refresh token
    String refreshToken = jwtUtils.generateRefreshToken();
    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setToken(refreshToken);
    refreshTokenEntity.setUser(user);
    refreshTokenEntity.setCreatedAt(LocalDateTime.now());
    refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(20));
    refreshTokenRepository.save(refreshTokenEntity);


    return ResponseEntity.ok(new MessageResponse("User CREATED"));
}

//    @PostMapping("/signin")
//    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
//        String email = loginRequest.getEmail();
//        String password = loginRequest.getPassword();
//        if (password == null || password.trim().isEmpty()) {
//            return ResponseEntity.badRequest().body("Password is empty or null");
//        }
//
//        Authentication authentication = authenticationManager
//                .authenticate(new UsernamePasswordAuthenticationToken(
//                        email,
//                        password));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtUtils.generateJwtToken(authentication);
//
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        return ResponseEntity.ok(new JwtResponse(
//                jwt,
//                userDetails.getId(),
//                userDetails.getEmail()));
//    }


    @PostMapping("/signin")
    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = new ArrayList<>();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                roles));

    }



    @PostConstruct
    public void adminReg() {
        if (userRepository.existsByEmail("superadmin@gmail.com")) {

        } else {
            String email = "superadmin@gmail.com";
            String password = "superadmin";
            User user = new User(email, passwordEncoder.encode(password));
            user.setIs_superuser(true);
            userRepository.save(user);
        }
    }







}
