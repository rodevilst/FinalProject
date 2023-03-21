package finalproject.controllers;

import finalproject.jwt.JwtUtils;
import finalproject.models.*;
import finalproject.pojo.JwtResponse;
import finalproject.pojo.LoginRequest;
import finalproject.pojo.MessageResponse;
import finalproject.pojo.SignUpRequest;
import finalproject.repository.AccessTokenRepository;
import finalproject.repository.ProfileRepository;
import finalproject.repository.RefreshTokenRepository;
import finalproject.repository.UserRepository;
import finalproject.service.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
//        String refreshToken = jwtUtils.generateRefreshToken(user);
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
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not found or blocked"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })

    @PostMapping("/signin")
    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (StringUtils.isBlank(password)) {
            return ResponseEntity.badRequest().body("password can`t be null");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            user.setLast_login(new Date());
            user.setIs_active(true);
            userRepository.save(user);

            String accessTokenJwt = jwtUtils.generateAccessToken(user);
            String refreshTokenJwt = jwtUtils.generateRefreshToken(user);

            AccessToken accessToken = accessTokenRepository.findByUserAndExpiresAtAfter(user, LocalDateTime.now());
            RefreshToken refreshToken = refreshTokenRepository.findByUserAndExpiresAtAfter(user, LocalDateTime.now());

            if (accessToken != null) {
                JwtResponse jwtResponse = createJwtResponse(user, accessToken.getToken(), refreshToken.getToken());
                return ResponseEntity.ok(jwtResponse);
            }
            if (accessToken == null && refreshToken != null) {
                String newAccessTokenJwt = jwtUtils.generateAccessToken(user);
                AccessToken newAccessToken = new AccessToken();
                newAccessToken.setToken(newAccessTokenJwt);
                newAccessToken.setUser(user);
                newAccessToken.setCreatedAt(LocalDateTime.now());
                newAccessToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));
                accessTokenRepository.save(newAccessToken);
                return ResponseEntity.ok(createJwtResponse(user, newAccessTokenJwt, refreshToken.getToken()));
            }
            if (accessToken == null && refreshToken == null) {
                AccessToken newAccessToken = createAccessToken(user, accessTokenJwt);
                RefreshToken newRefreshToken = createRefreshToken(user, refreshTokenJwt);
                JwtResponse jwtResponse = createJwtResponse(user, newAccessToken.getToken(), newRefreshToken.getToken());
                return ResponseEntity.ok(jwtResponse);
            }

            if (refreshToken != null) {
                AccessToken newAccessToken = createAccessToken(user, accessTokenJwt);
                refreshToken.setToken(refreshTokenJwt);
                refreshToken.setCreatedAt(LocalDateTime.now());
                refreshToken.setExpiresAt(LocalDateTime.now().plusMinutes(20));
                refreshTokenRepository.save(refreshToken);
                JwtResponse jwtResponse = createJwtResponse(user, newAccessToken.getToken(), refreshTokenJwt);
                return ResponseEntity.ok(jwtResponse);
            }


            throw new RuntimeException("Something went wrong");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("user not found");
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("email or password incorrect");
        } catch (LockedException e) {
            return ResponseEntity.badRequest().body("user blocked");
        } catch (DisabledException e) {
            return ResponseEntity.badRequest().body(null); // Ошибка: пользователь отключен
        }
    }

@Operation(summary = "give new token",
        operationId = "newtoken",
        responses = {
                @ApiResponse(responseCode = "200", description = "OK",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = JwtResponse.class))),
                @ApiResponse(responseCode = "400", description = "Bad request")
        })
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(@RequestBody TokenWrapper tokenWrapper) {
    String refreshToken = tokenWrapper.getToken();
    RefreshToken byToken = refreshTokenRepository.findByToken(refreshToken);
    if (byToken == null) {
        return new ResponseEntity<>("Invalid refresh token", HttpStatus.BAD_REQUEST);
    }

    User user = byToken.getUser();
    String accessTokenJwt = jwtUtils.generateAccessToken(user);
    String refreshTokenJwt = jwtUtils.generateRefreshToken(user);

    LocalDateTime now = LocalDateTime.now();
    AccessToken accessTokens = accessTokenRepository.findByUserAndExpiresAtAfter(user, now);
    if (accessTokens != null) {
        accessTokenRepository.delete(accessTokens);
    }

    if (byToken.getExpiresAt().isBefore(now)) {
        return new ResponseEntity<>("Refresh token has expired", HttpStatus.BAD_REQUEST);
    } else {
        refreshTokenRepository.delete(byToken);
        RefreshToken refreshTokenEntity = createRefreshToken(user, refreshTokenJwt);
        AccessToken accessToken = createAccessToken(user, accessTokenJwt);
        JwtResponse jwtResponse = createJwtResponse(user,accessToken.getToken(),refreshTokenEntity.getToken());
        return new ResponseEntity<>(jwtResponse,HttpStatus.OK);
    }
}
    private AccessToken createAccessToken(User user, String accessTokenJwt) {
        AccessToken accessTokenEntity = new AccessToken();
        accessTokenEntity.setToken(accessTokenJwt);
        accessTokenEntity.setUser(user);
        accessTokenEntity.setCreatedAt(LocalDateTime.now());
        accessTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        return accessTokenRepository.save(accessTokenEntity);
    }
    private RefreshToken createRefreshToken(User user,String refreshTokenJwt) {
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshTokenJwt);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(20));
        return refreshTokenRepository.save(refreshTokenEntity);
    }
    private JwtResponse createJwtResponse(User user, String accessToken, String refreshToken) {
        user.setLast_login(new Date());
        user.setIs_active(true);
        userRepository.save(user);

        JwtResponse jwtResponse = new JwtResponse();
        BeanUtils.copyProperties(user, jwtResponse);
        jwtResponse.setAccess_token(accessToken);
        jwtResponse.setRefresh_token(refreshToken);

        return jwtResponse;
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
