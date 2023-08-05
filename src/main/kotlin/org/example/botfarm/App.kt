package org.example.botfarm

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import org.slf4j.LoggerFactory

object App {
    private val logger = LoggerFactory.getLogger(javaClass)

    // 1. bot name; 2. bot token; 3. appid from openweathermap.org.
    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("application starting...")

        val bot = bot {
            token = args[1]
            dispatch {
                command("start") {
                    val result = bot.sendMessage(
                        chatId = ChatId.fromId(update.message!!.chat.id),
                        text = "Bot started"
                    )

                    result.fold(
                        {
                            // do something here with the response
                        },
                        {
                            // do something with the error
                        },
                    )
                }
                text {
                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = text)
                }
                text("ping") {
                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Pong")
                }
            }
        }

        bot.startPolling()
        logger.info("bot successfully started")
    }
}
