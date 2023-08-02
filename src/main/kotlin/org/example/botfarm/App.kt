package org.example.botfarm

object App {
    @JvmStatic
    fun main(args: Array<String>) {
        // 1. bot name; 2. bot token; 3. appid from openweathermap.org.
        val firstBot = Bot(args[0], args[1], args[2])
        firstBot.botConnect()
    }
}
