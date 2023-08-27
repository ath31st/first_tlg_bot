package org.example.botfarm

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.location
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove
import com.github.kotlintelegrambot.logging.LogLevel
import org.example.botfarm.service.AufService
import org.example.botfarm.service.JokeService
import org.example.botfarm.service.WeatherService
import org.example.botfarm.util.State
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

object AppKt {
    private val logger = LoggerFactory.getLogger(javaClass)

    // 1. bot token; 2. appid from openweathermap.org.
    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("application starting...")
        val botToken = args[0]
        val weatherAppid = args[1]

        val userStateMap: ConcurrentHashMap<Long, State> = ConcurrentHashMap()
        val jokeService = JokeService()
        val weatherService = WeatherService(weatherAppid)
        val aufService = AufService()

        val bot = bot {
            logLevel = LogLevel.Error
            token = botToken
            dispatch {
                text {
                    val chatId = ChatId.fromId(update.message!!.chat.id)
                    if (userStateMap[chatId.id] == State.WEATHER) {
                        bot.sendMessage(
                            chatId = chatId,
                            text = weatherService.getForecast(text)
                        )
                        userStateMap[update.message!!.chat.id] = State.DEFAULT
                    }
                }
                command("start") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = "Bot started"
                    )
                    userStateMap[update.message!!.chat.id] = State.DEFAULT
                }
                command("joke") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = jokeService.getJoke()
                    )
                }
                command("auf") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = aufService.getAuf()
                    )
                }
                command("weather") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = "Введите название города: "
                    )
                    userStateMap[update.message!!.chat.id] = State.WEATHER
                }
                location {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = weatherService.getForecast(
                            this.location.latitude,
                            this.location.longitude
                        ),
                        replyMarkup = ReplyKeyboardRemove(),
                    )
                }
                telegramError {
                    println(error.getErrorMessage())
                }
            }
        }

        bot.startPolling()
        logger.info("bot successfully started")
    }
}
