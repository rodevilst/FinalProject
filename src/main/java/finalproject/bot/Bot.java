package finalproject.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
                message.setText("Выберите цвет:");

                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                keyboardMarkup.setSelective(true);
                keyboardMarkup.setResizeKeyboard(true);
                keyboardMarkup.setOneTimeKeyboard(false);

                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow row1 = new KeyboardRow();
                row1.add(new KeyboardButton("Красный"));
                row1.add(new KeyboardButton("Зеленый"));
                keyboard.add(row1);

                KeyboardRow row2 = new KeyboardRow();
                row2.add(new KeyboardButton("Синий"));
                row2.add(new KeyboardButton("Желтый"));
                keyboard.add(row2);

                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);

                try {
                    execute(message); // отправить сообщение
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (text.equals("Красный")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("Ты выбрал красный");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (text.equals("Зеленый")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("Ты выбрал зеленый");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (text.equals("Синий")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("Ты выбрал синий");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (text.equals("Желтый")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("Ты выбрал желтый");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
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
