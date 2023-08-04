package org.example.botfarm

import org.slf4j.LoggerFactory

object App {
    private val logger = LoggerFactory.getLogger(javaClass)

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("application starting...")
        // 1. bot name; 2. bot token; 3. appid from openweathermap.org.
        val firstBot = Bot(args[0], args[1], args[2])
        logger.info("bot object successfully created")

        firstBot.botConnect()
    }
}
