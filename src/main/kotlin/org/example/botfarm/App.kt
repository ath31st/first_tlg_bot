package org.example.botfarm

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.location
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove
import org.example.botfarm.service.JokeService
import org.example.botfarm.service.WeatherService
import org.example.botfarm.util.State
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

object App {
    private val logger = LoggerFactory.getLogger(javaClass)

    // 1. bot token; 2. appid from openweathermap.org.
    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("application starting...")
        val userStateMap: ConcurrentHashMap<Long, State> = ConcurrentHashMap()
        val jokeService = JokeService()
        val weatherService = WeatherService(args[1])

        val bot = bot {
            token = args[0]
            dispatch {
                text {
                    val chatId = ChatId.fromId(update.message!!.chat.id)
                    val textMessage = when (userStateMap[chatId.id]) {
                        State.WEATHER -> weatherService.getForecastByCity(text)
                        State.DEFAULT -> text
                        null -> text
                    }

                    val result = bot.sendMessage(
                        chatId = chatId,
                        text = textMessage
                    )
                    result.fold(
                        {
                            userStateMap[update.message!!.chat.id] = State.DEFAULT
                        },
                        {
                            userStateMap[update.message!!.chat.id] = State.DEFAULT
                        },
                    )
                }
                command("start") {
                    val result = bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = "Bot started"
                    )
                    result.fold(
                        {
                            userStateMap[update.message!!.chat.id] = State.DEFAULT
                        },
                        {
                            // do something with the error
                            println("failure")
                        },
                    )
                }
                command("joke") {
                    bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = jokeService.getJoke()
                    )
                }
                command("weather") {
                    val result = bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = "Введите название города: "
                    )
                    result.fold(
                        {
                            userStateMap[update.message!!.chat.id] = State.WEATHER
                        },
                        {

                        },
                    )

                }
                location {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Your location is (${location.latitude}, ${location.longitude})",
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
