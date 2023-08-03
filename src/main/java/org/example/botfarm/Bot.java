//package org.example.botfarm;
//
//
//import lombok.*;
//import org.apache.log4j.Logger;
//import org.example.botfarm.service.*;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Getter
//@Setter
//@RequiredArgsConstructor
//public class Bot extends TelegramLongPollingBot {
//    private static final Logger log = Logger.getLogger(Bot.class);
//    private final int RECONNECT_PAUSE = 10000;
//    private final String BOT_NAME;
//    private final String BOT_TOKEN;
//    private final String WEATHER_APPID;
//
//
//    @Override
//    public String getBotUsername() {
//        return BOT_NAME;
//    }
//
//    @Override
//    public String getBotToken() {
//        return BOT_TOKEN;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//
//        log.debug("new Update receive. ID: " + update.getUpdateId());
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            Long chatId = update.getMessage().getChatId();
//            String inputText = update.getMessage().getText().split("@")[0];
//
//            ServiceFactoryImpl serviceFactory = new ServiceFactoryImpl(WEATHER_APPID);
//            Service service = serviceFactory.makeService(inputText);
//            sendMsg(String.valueOf(chatId), service.getResult());
//        }
//    }
//
//    public void botConnect() {
//        try {
//            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//            telegramBotsApi.registerBot(this);
//
//            log.info("TelegramAPI started. Look for messages");
//
//        } catch (TelegramApiException e) {
//            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
//            try {
//                Thread.sleep(RECONNECT_PAUSE);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//                return;
//            }
//            botConnect();
//        }
//    }
//
//    private synchronized void sendMsg(String chatId, String msg) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.enableMarkdown(true);
//        sendMessage.enableHtml(true);
//       // setButtons(sendMessage);
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(msg);
//        try {
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//            log.error("error in " + chatId + " with next message: " + msg + "\n" + Arrays.toString(e.getStackTrace()) + "\n" +
//                    e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    private synchronized void setButtons(SendMessage sendMessage) {
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        sendMessage.setReplyMarkup(replyKeyboardMarkup);
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(false);
//
//        List<KeyboardRow> keyboard = new ArrayList<>();
//
//        KeyboardRow keyboardFirstRow = new KeyboardRow();
//        keyboardFirstRow.add(new KeyboardButton("/погода в Москве"));
//        keyboardFirstRow.add(new KeyboardButton("/анекдот с Bashorg.org"));
//
//        KeyboardRow keyboardSecondRow = new KeyboardRow();
//        keyboardSecondRow.add(new KeyboardButton("/помощь"));
//
//        keyboard.add(keyboardFirstRow);
//        keyboard.add(keyboardSecondRow);
//
//        replyKeyboardMarkup.setKeyboard(keyboard);
//    }
//}
