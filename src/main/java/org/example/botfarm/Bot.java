package org.example.botfarm;


import lombok.*;
import org.apache.log4j.Logger;
import org.example.botfarm.service.WeatherService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(Bot.class);

    private final int RECONNECT_PAUSE = 10000;
    private final String BOT_NAME;
    private final String BOT_TOKEN;
    private final String WEATHER_APPID;

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("new Update receive. ID: " + update.getUpdateId());

        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();

        if (inputText.startsWith("/start")) {
            String msg = "Вас приветствует бот " + BOT_NAME;
            sendMsg(String.valueOf(chatId), msg);
        } else if (inputText.startsWith("/анекдот")) {
            String msg = update.getMessage().getFrom().getFirstName() + " шуточки пришел почитать? давай работай бегом!";
            sendMsg(String.valueOf(chatId), msg);
        } else if (inputText.startsWith("/погода")) {
            WeatherService weatherService = new WeatherService(WEATHER_APPID);
            String msg = weatherService.getWeather("Poltava");
            sendMsg(String.valueOf(chatId), msg);
        } else if (inputText.startsWith("/помощь")) {
            String msg = "Бот обрабатывает следующие команды: \n" +
                    "1. /start\n" +
                    "2. /do_something\n";
            sendMsg(String.valueOf(chatId), msg);
        } else {
            String msg = "Не понял вас, используйте список команд или кнопки";
            sendMsg(String.valueOf(chatId), msg);
        }
    }

    public void botConnect() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);

            log.info("TelegramAPI started. Look for messages");

        } catch (TelegramApiException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }

    /**
     * Метод для настройки сообщения и его отправки.
     *
     * @param chatId id чата
     * @param msg    строка, которую необходимо отправить в качестве сообщения.
     */
    public synchronized void sendMsg(String chatId, String msg) {
        SendMessage sendMessage = new SendMessage();
        // sendMessage.enableMarkdown(true);
        setButtons(sendMessage);
        sendMessage.setChatId(chatId);
        sendMessage.setText(msg);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("/анекдот"));
        keyboardFirstRow.add(new KeyboardButton("/погода"));
        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("/помощь"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
