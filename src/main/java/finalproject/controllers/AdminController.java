package finalproject.controllers;

import finalproject.jwt.JwtUtils;
import finalproject.models.*;
import finalproject.pojo.ActivateUser;
import finalproject.pojo.JwtResponse;
import finalproject.pojo.MessageResponse;
import finalproject.pojo.SignUpRequest;
import finalproject.repository.AccessTokenRepository;
import finalproject.repository.ProfileRepository;
import finalproject.repository.RefreshTokenRepository;
import finalproject.repository.UserRepository;
import finalproject.service.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Api(value = "Get users")

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessTokenRepository accessTokenRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    ProfileRepository profileRepository;

    @Operation(summary = "get user",
            operationId = "getuser",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<User> all = userRepository.findAll();
        all.removeIf(user -> user.getEmail().equals(userDetails.getEmail()));
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @SecurityRequirement(name = "JWT")

    @GetMapping("/user")
    @Operation(summary = "Get authenticated user",
            operationId = "Getauthenticateduser",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    public ResponseEntity<?> getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetailsImpl) principal).getEmail();
            Optional<User> byEmail = userRepository.findByEmail(email);
            return new ResponseEntity<>(byEmail, HttpStatus.OK);
        }
        return new ResponseEntity<>("User details not found", HttpStatus.NOT_FOUND);
    }
    @PreAuthorize("#user.isIs_superuser()")
    @PostMapping("/users/reg")
    @Operation(summary = "Create a new user",
            operationId = "CreateUser",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccessToken.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - missing required fields or invalid email format"),
                    @ApiResponse(responseCode = "409", description = "Email already exists")
            })
    public ResponseEntity<?> createUser(@RequestBody SignUpRequest signUpRequest,@AuthenticationPrincipal UserDetailsImpl user) {
        String randomPassword = generateRandomPassword(20);
        if (StringUtils.isBlank(signUpRequest.getEmail())
                || StringUtils.isBlank(signUpRequest.getName())
                || StringUtils.isBlank(signUpRequest.getUsername())) {
            return new ResponseEntity<>("All fields are required", HttpStatus.BAD_REQUEST);
        }
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!Pattern.matches(emailRegex, signUpRequest.getEmail())) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error : Email is exist"));
        }
        User newuser = new User(signUpRequest.getEmail());
        newuser.setPassword(passwordEncoder.encode(randomPassword));

        Profile profile = new Profile(signUpRequest.getName(), signUpRequest.getUsername());
        profile.setUser(newuser);
        userRepository.save(newuser);
        profileRepository.save(profile);

        String accessToken = jwtUtils.generateAccessToken(newuser);
        AccessToken accessTokenEntity = new AccessToken();
        accessTokenEntity.setToken(accessToken);
        accessTokenEntity.setUser(newuser);
        accessTokenEntity.setCreatedAt(LocalDateTime.now());
        accessTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        accessTokenRepository.save(accessTokenEntity);

        String refreshToken = jwtUtils.generateRefreshToken(newuser);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(newuser);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(20));
        refreshTokenRepository.save(refreshTokenEntity);
        userRepository.save(newuser);
        return new ResponseEntity<>(accessTokenEntity, HttpStatus.CREATED);
    }
    @Operation(summary = "Access token",
            description = "Refreshes an existing access token by generating a new one and deleting the old one.",
            operationId = "accessToken",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TokenWrapper.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid access token")
            })
    @PostMapping("/users/recreate")
    @PreAuthorize("#userDetails.isIs_superuser()")

    public ResponseEntity<?> accessToken(@RequestBody TokenWrapper tokenWrapper,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String accessToken = tokenWrapper.getToken();
        AccessToken byToken = accessTokenRepository.findByToken(accessToken);
        if (byToken == null) {
            return new ResponseEntity<>("Invalid access token", HttpStatus.BAD_REQUEST);
        }

        User user = byToken.getUser();
        String newAccessTokenJwt = jwtUtils.generateAccessToken(user);

        LocalDateTime now = LocalDateTime.now();
        AccessToken oldAccessToken = accessTokenRepository.findByUserAndExpiresAtAfter(user, now);
        if (oldAccessToken != null) {
            accessTokenRepository.delete(oldAccessToken);
        }

        AccessToken newAccessToken = new AccessToken();
        newAccessToken.setToken(newAccessTokenJwt);
        newAccessToken.setUser(user);
        newAccessToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        accessTokenRepository.save(newAccessToken);

        return ResponseEntity.ok(new TokenWrapper(newAccessTokenJwt));
    }
    @Operation(summary = "Activate user with provided token and set password",
            operationId = "ActivateUser",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User activated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid access token"),
                    @ApiResponse(responseCode = "404", description = "Token not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/activate/{token}")
    public ResponseEntity<?> activateUser(@RequestBody ActivateUser activateUser,
                                          @PathVariable String token) {
        if (token == null) {
            return new ResponseEntity<>("Invalid access token", HttpStatus.BAD_REQUEST);
        }
        AccessToken byToken = accessTokenRepository.findByToken(token);
        User user = byToken.getUser();
        String password = activateUser.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        System.out.println(password);
        user.setIs_active(true);
        user.setCreated(new Date());
        userRepository.save(user);
        return ResponseEntity.ok().body("User created");
    }

    public static String generateRandomPassword(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }


}

