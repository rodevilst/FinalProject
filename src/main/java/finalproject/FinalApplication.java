package finalproject;

import finalproject.configs.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@Import(SwaggerConfig.class)
public class FinalApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FinalApplication.class, args);
    }
}