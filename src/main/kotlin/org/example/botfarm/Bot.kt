package org.example.botfarm

import org.example.botfarm.service.Factory
import org.example.botfarm.service.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


class Bot(
    private val botName: String,
    private val botToken: String,
    private val weatherAppid: String
) : TelegramLongPollingBot() {
    private val reconnectPause = 10000

    override fun getBotUsername(): String {
        return botName
    }

    override fun getBotToken(): String {
        return botToken
    }

    override fun onUpdateReceived(update: Update?) {
        if (update!!.hasMessage() && update.message.hasText()) {
            val chatId = update.message.chatId
            val inputText = update.message.text.split("@".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]
            val serviceFactory = Factory(weatherAppid)
            val service: Service = serviceFactory.makeService(inputText)
            sendMsg(chatId.toString(), service.getResult())
        }
    }

    fun botConnect() {
        try {
            val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
            telegramBotsApi.registerBot(this)
        } catch (e: TelegramApiException) {
            try {
                Thread.sleep(reconnectPause.toLong())
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
                return
            }
            botConnect()
        }
    }

    @Synchronized
    private fun sendMsg(chatId: String, msg: String) {
        val sendMessage = SendMessage()
        sendMessage.enableMarkdown(true)
        sendMessage.enableHtml(true)
        // setButtons(sendMessage);
        sendMessage.chatId = chatId
        sendMessage.text = msg
        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            throw RuntimeException(e)
        }
    }
}