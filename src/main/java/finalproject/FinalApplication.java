package finalproject;

import finalproject.bot.Bot;
import finalproject.configs.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@Import(SwaggerConfig.class)
public class FinalApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FinalApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }



    }
}