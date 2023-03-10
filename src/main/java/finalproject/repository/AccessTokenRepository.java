package finalproject.repository;

import antlr.Token;
import finalproject.models.AccessToken;
import finalproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    AccessToken findByToken(String token);
    List<AccessToken> findAllByUserId(Long userId);
    AccessToken findByUserAndExpiresAtAfter(User user, LocalDateTime expiresAt);
}
