package org.example.botfarm;


import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(Bot.class);

    final int RECONNECT_PAUSE = 10000;
    private final String BOT_NAME;
    private final String BOT_TOKEN;

    public Bot(String botName, String token) {
        this.BOT_NAME = botName;
        this.BOT_TOKEN = token;
    }

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
     * @param msg строка, которую необходимо отправить в качестве сообщения.
     */
    public void sendMsg(String chatId, String msg) {
        SendMessage sendMessage = new SendMessage();
       // sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(msg);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
