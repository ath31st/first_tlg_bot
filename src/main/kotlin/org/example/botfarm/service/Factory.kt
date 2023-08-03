package org.example.botfarm.service

class Factory(private val weatherAppid: String) : ServiceFactory {

    override fun makeService(nameService: String): Service {

        val weatherService = WeatherService(weatherAppid)
        return when (nameService) {
            "/start" -> object : Service() {
                override fun getResult(): String {
                    return "Вас приветствует бот First bot. " +
                            "Моя задача подсказать вам прогноз погоды на ближайшие сутки и немного развлечь вас. " +
                            "Укажите название города в сообщении."
                }
            }

            "/помощь" -> object : Service() {
                override fun getResult(): String {
                    return """
                        Бот обрабатывает следующие команды: 
                        /start
                        /joke
                        /weather
                        
                        """.trimIndent()
                }
            }

            "/joke", "/анекдот с Bashorg.org" -> JokeService()
            "Путин", "путин" -> object : Service() {
                override fun getResult(): String {
                    return "При чем тут Путин?"
                }
            }

            "/weather", "/погода в Москве" -> {
                weatherService.city = "Москва"
                weatherService
            }

            else -> {
                weatherService.city = nameService
                weatherService
            }
        }
    }
}