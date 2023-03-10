package finalproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalApplication.class, args);
    }
}