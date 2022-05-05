package org.example.botfarm;


import lombok.*;
import org.apache.log4j.Logger;
import org.example.botfarm.service.JokeService;
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

        if (inputText == null) {
            String msg = "Сообщение не является текстом. Картинки, стикеры и прочее непотребство я еще не умею различать.";
            sendMsg(String.valueOf(chatId), msg);
        } else if (inputText.startsWith("/start")) {
            String msg = "Вас приветствует бот First bot. " +
                    "Моя задача подсказать вам прогноз погоды на ближайшие сутки и немного развлечь вас. " +
                    "Укажите название города в сообщении.";
            sendMsg(String.valueOf(chatId), msg);
        } else if (inputText.startsWith("/анекдот") | inputText.startsWith("/joke")) {
            JokeService jokeService = new JokeService();
            String msg = jokeService.getRandomJoke();
            sendMsg(String.valueOf(chatId), msg);
        } else if (inputText.startsWith("/погода в Москве") | inputText.startsWith("/weather")) {
            WeatherService weatherService = new WeatherService(WEATHER_APPID);
            String msg = weatherService.getForecast("Москва");
            sendMsg(String.valueOf(chatId), msg);
        } else if (inputText.startsWith("/помощь")) {
            String msg = "Бот обрабатывает следующие команды: \n" +
                    "/start\n" +
                    "/joke\n" +
                    "/weather\n";
            sendMsg(String.valueOf(chatId), msg);
        } else {
            String city = update.getMessage().getText();
            WeatherService weatherService = new WeatherService(WEATHER_APPID);
            String msg = weatherService.getForecast(city);
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

    public synchronized void sendMsg(String chatId, String msg) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
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
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("/анекдот с Bash.org"));
        keyboardFirstRow.add(new KeyboardButton("/погода в Москве"));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("/помощь"));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
