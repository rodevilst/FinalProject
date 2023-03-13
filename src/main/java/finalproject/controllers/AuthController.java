package finalproject.controllers;

import finalproject.jwt.JwtUtils;
import finalproject.models.AccessToken;
import finalproject.models.Profile;
import finalproject.models.RefreshToken;
import finalproject.models.User;
import finalproject.pojo.JwtResponse;
import finalproject.pojo.LoginRequest;
import finalproject.repository.AccessTokenRepository;
import finalproject.repository.ProfileRepository;
import finalproject.repository.RefreshTokenRepository;
import finalproject.repository.UserRepository;
import finalproject.service.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import org.slf4j.Logger;

@Api(value = "Reg && auth controller")
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

//    @Operation(summary = "Register user",
//            operationId = "regUser",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "OK"),
//                    @ApiResponse(responseCode = "400", description = "Bad request")
//            })
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
//        if (signUpRequest.getPassword().isEmpty()) {
//
//        } else {
//            String encodedPassword = new BCryptPasswordEncoder().encode(signUpRequest.getPassword());
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
//
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        User user = new User(signUpRequest.getEmail(),
//                encoder.encode(signUpRequest.getPassword()));
//        user.setCreated(new Date());
//        Profile profile = new Profile();
//        profile.setUser(user);
//        userRepository.save(user);
//        profileRepository.save(profile);
//        String password = signUpRequest.getPassword();
//
//        if (encoder.matches(password, user.getPassword())) {
//            System.out.println("kodir");
//        }
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

    @Operation(summary = "Authenticate user",
            operationId = "authUser",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })

    @PostMapping("/signin")
    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is empty or null");
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        email,
                        password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        String accessTokenJwt = jwtUtils.generateAccessToken(user);
        String refreshTokenJwt = jwtUtils.generateRefreshToken(user);
        AccessToken accessToken = accessTokenRepository.findByUserAndExpiresAtAfter(user, LocalDateTime.now());
        RefreshToken refreshToken = refreshTokenRepository.findByUserAndExpiresAtAfter(user, LocalDateTime.now());

        if (accessToken != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            user.setLast_login(new Date());
            user.setIs_active(true);
            userRepository.save(user);
            JwtResponse jwtResponse = new JwtResponse();
            BeanUtils.copyProperties(user, jwtResponse);
            jwtResponse.setAccess_token(accessToken.getToken());
            jwtResponse.setRefresh_token(refreshToken.getToken());
            return ResponseEntity.ok(jwtResponse);
        } else if (accessToken == null) {
            user.setLast_login(new Date());
            user.setIs_active(true);
            userRepository.save(user);
            JwtResponse jwtResponse = new JwtResponse();
            BeanUtils.copyProperties(user, jwtResponse);
            jwtResponse.setAccess_token(accessTokenJwt);
            jwtResponse.setRefresh_token(refreshTokenJwt);
            AccessToken accessTokenEntity = new AccessToken();
            accessTokenEntity.setToken(accessTokenJwt);
            accessTokenEntity.setUser(user);
            accessTokenEntity.setCreatedAt(LocalDateTime.now());
            accessTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            accessTokenRepository.save(accessTokenEntity);
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(refreshTokenJwt);
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setCreatedAt(LocalDateTime.now());
            refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(20));
            refreshTokenRepository.save(refreshTokenEntity);
            return ResponseEntity.ok(jwtResponse);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        user.setLast_login(new Date());
        userRepository.save(user);
        JwtResponse jwtResponse = new JwtResponse();
        BeanUtils.copyProperties(user, jwtResponse);
        jwtResponse.setAccess_token(accessTokenJwt);
        jwtResponse.setRefresh_token(refreshTokenJwt);
        return ResponseEntity.ok(jwtResponse);

    }


    @PostConstruct
    public void adminReg() {
        if (userRepository.existsByEmail("superadmin@gmail.com")) {

        } else {
            String email = "superadmin@gmail.com";
            String password = "superadmin";
            User user = new User(email, passwordEncoder.encode(password));
            user.setIs_superuser(true);
            user.setCreated(new Date());
            userRepository.save(user);
            Profile profile = new Profile();
            profile.setUser(user);
            profile.setName("Admin");
            profile.setUsername("Admin");
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
            String refreshToken = jwtUtils.generateRefreshToken(user);
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setCreatedAt(LocalDateTime.now());
            refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(20));
            refreshTokenRepository.save(refreshTokenEntity);
        }
    }


}
