package finalproject.controllers;

import finalproject.jwt.JwtUtils;
import finalproject.models.AccessToken;
import finalproject.models.User;
import finalproject.repository.AccessTokenRepository;
import finalproject.repository.UserRepository;
import finalproject.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Api(value = "Get users")

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessTokenRepository accessTokenRepository;

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
    public ResponseEntity<?> getAllUsers() {
        List<User> all = userRepository.findAll();
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
            String email = ((UserDetailsImpl)principal).getEmail();
            Optional<User> byEmail = userRepository.findByEmail(email);
            return new ResponseEntity<>(byEmail, HttpStatus.OK);
        }
        return new ResponseEntity<>("User details not found", HttpStatus.NOT_FOUND);
    }



}

