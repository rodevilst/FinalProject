package finalproject.bot;

import finalproject.controllers.AuthController;
import finalproject.pojo.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();


        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("Введите логин");
                try {
                    execute(message); // отправляем сообщение с запросом логина пользователю
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (text != null) {
                // получаем логин пользователя
                String login = text;

                // отправляем сообщение с запросом пароля пользователю
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("Введите пароль");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                // получаем пароль пользователя
                String password = update.getMessage().getText();

                // создаем объект AuthController и вызываем метод authUser
                AuthController authController = new AuthController();
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail(login);
                loginRequest.setPassword(password);
                authController.authUser(loginRequest);

                boolean isAuthenticated = authController.authUser(loginRequest).getStatusCode().is2xxSuccessful();

                // отправляем сообщение о результате аутентификации
                message.setText(isAuthenticated ? "Вы успешно авторизованы" : "Неверный логин или пароль");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }






    @Override
    public String getBotUsername() {
        // TODO
        return "rodevilst_bot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "6235129573:AAFmOtLffLtVb13BjAvv-JH7vdvTP2M7VWc";
    }
}
