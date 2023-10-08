package org.example.botfarm

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.location
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.logging.LogLevel
import java.util.concurrent.ConcurrentHashMap
import org.example.botfarm.service.AufService
import org.example.botfarm.service.JokeService
import org.example.botfarm.service.WeatherService
import org.example.botfarm.util.State
import org.slf4j.LoggerFactory

/**
 * This object represents the main entry point of the application.
 * It initializes and configures a Telegram bot for various commands and functionalities.
 */
object AppKt {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * The main function for starting the application.
     *
     * @param args An array of command-line arguments, including bot token and OpenWeatherMap
     * API key.
     */
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
                            text = weatherService.getForecast(text),
                        )
                        userStateMap[update.message!!.chat.id] = State.DEFAULT
                    }
                }
                command("start") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = "Bot started",
                    )
                }
                command("joke") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = jokeService.getJoke(),
                    )
                }
                command("auf") {
                    bot.sendPhoto(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        caption = aufService.getAuf(),
                        photo = TelegramFile.ByFile(
                            aufService.getRandomAufFileFromResources()!!,
                        ),
                    )
                }
                command("weather") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = "Введите название города: ",
                    )
                    userStateMap[update.message!!.chat.id] = State.WEATHER
                }
                location {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = weatherService.getForecast(
                            this.location.latitude,
                            this.location.longitude,
                        ),
                        replyMarkup = ReplyKeyboardRemove(),
                    )
                }
                telegramError {
                    println(error.getErrorMessage())
                }
                clearUserStateMap(userStateMap)
            }
        }

        bot.startPolling()
        logger.info("bot successfully started")
    }
}

/**
 * Clears the user state map by removing entries with the default state.
 *
 * @param userStateMap The ConcurrentHashMap containing user states.
 */
private fun clearUserStateMap(userStateMap: ConcurrentHashMap<Long, State>) {
    if (userStateMap.isNotEmpty()) {
        val iterator = userStateMap.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value == State.DEFAULT) {
                iterator.remove()
            }
        }
    }
}
