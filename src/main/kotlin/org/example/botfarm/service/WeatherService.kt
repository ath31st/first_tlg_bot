package org.example.botfarm.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

data class WeatherService(val appid: String, val city: String) : Service() {
    private val apiCallTemplate = "http://api.openweathermap.org/data/2.5/forecast?q="
    private val apiKeyTemplate = "&units=metric&APPID="
    private val userAgent = "Mozilla/5.0"
    private val inputDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val outputDateTimeFormat =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
    private lateinit var connection: HttpURLConnection


    override fun getResult(): String {
        val result: String
        try {
            connection = getConnectionToForecastApi(city)
            val rawJson = getRawJsonFromConnection(connection)
            val stringsForecasts = convertRawJsonToListForecasts(rawJson)
            result = String.format(
                "%s:%s%s",
                city,
                System.lineSeparator(),
                parseForecastJsonFromList(stringsForecasts)
            )
        } catch (e: IllegalArgumentException) {
            return String.format("Указано неправильное название города (%s)", city)
        } catch (e: java.lang.Exception) {
            // return "Problems connecting to the weather service.\nTry again later.";
            throw java.lang.RuntimeException(e)
        }
        return result
    }

    private fun getRawJsonFromConnection(connection: HttpURLConnection): String {
        val response = java.lang.StringBuilder()
        try {
            BufferedReader(InputStreamReader(connection.inputStream)).use { bufferedReader ->
                var inputLine: String?
                while (bufferedReader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
            }
        } catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        }
        return response.toString()
    }

    private fun getConnectionToForecastApi(city: String): HttpURLConnection {
        val urlString = apiCallTemplate + city + apiKeyTemplate + appid
        try {
            val urlObject = URL(urlString)
            connection = urlObject.openConnection() as HttpURLConnection
            connection.setRequestMethod("GET")
            connection.setRequestProperty("User-Agent", userAgent)
            connection.setConnectTimeout(3000)
            val responseCode = connection.getResponseCode()
            require(responseCode != 404)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return connection
    }

    @Throws(Exception::class)
    private fun convertRawJsonToListForecasts(rawJson: String): List<String?> {
        var weatherList: MutableList<String?> = ArrayList()
        val arrNode = ObjectMapper().readTree(rawJson)["list"]
        if (arrNode.isArray) {
            for (objNode in arrNode) {
                weatherList.add(objNode.toString())
            }
            weatherList = weatherList.stream().limit(17).collect(Collectors.toList())
        }
        return weatherList
    }

    private fun parseForecastJsonFromList(weatherList: List<String?>): String {
        val stringBuilder = StringBuilder()
        val objectMapper = ObjectMapper()
        for (line in weatherList) {
            run {
                var dateTime: String
                val mainNode: JsonNode
                val weatherArrNode: JsonNode
                try {
                    mainNode = objectMapper.readTree(line)["main"]
                    weatherArrNode = objectMapper.readTree(line)["weather"]
                    for (objNode in weatherArrNode) {
                        dateTime = objectMapper.readTree(line)["dt_txt"].toString()
                        stringBuilder.append(
                            formatForecastData(
                                dateTime,
                                objNode["main"].toString(),
                                mainNode["temp"].asDouble()
                            )
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun formatForecastData(
        dateTime: String,
        description: String,
        temperature: Double
    ): String {
        val forecastDateTime = LocalDateTime.parse(
            dateTime.replace("\"".toRegex(), ""),
            inputDateTimeFormat
        )
        val formattedDateTime = forecastDateTime.format(outputDateTimeFormat)
        val formattedTemperature: String
        val roundedTemperature = Math.round(temperature)
        formattedTemperature = if (roundedTemperature > 0) {
            "+" + Math.round(temperature)
        } else {
            Math.round(temperature).toString()
        }
        val formattedDescription = description.replace("\"".toRegex(), "")
        val weatherUnicode = convertDescriptionToUnicode(formattedDescription)
        return String.format(
            "%s %5s %-4s %s%s", formattedDateTime,
            formattedTemperature, weatherUnicode, formattedDescription, System.lineSeparator()
        )
    }

    private fun convertDescriptionToUnicode(description: String): String {
        return when (description) {
            "Clouds" -> "☁"
            "Clear" -> "☀"
            "Snow" -> "\uD83C\uDF28"
            "Rain" -> "\uD83C\uDF27"
            "Drizzle" -> "\uD83C\uDF26"
            "Thunderstorm" -> "⛈"
            "Atmosphere" -> "\uD83C\uDF2B"
            else -> description
        }
    }
}