package org.example.botfarm.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.example.botfarm.service.forecast.Forecast
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class WeatherService(private val appid: String) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val apiCallTemplate = "http://api.openweathermap.org/data/2.5/forecast?q="
    private val apiCallTemplateWithLatLon = "http://api.openweathermap.org/data/2.5/forecast?q="
    private val apiKeyTemplate = "&units=metric&APPID="
    private val userAgent = "Mozilla/5.0"
    private val inputDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val outputDateTimeFormat =
        DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.forLanguageTag("ru-RU"))

//    fun getForecastByCoordinates(latitude: Double, longitude: Double): String {
//        val result: String = try {
//            val con = getConnectionToForecastApiByCoordinates(latitude, longitude)
//            val rawJson = getRawJsonFromConnection(con)
//            val stringsForecasts = convertRawJsonToListForecasts(rawJson)
//            "$city:\n${parseForecastJsonFromList(stringsForecasts)}"
//        } catch (e: IllegalArgumentException) {
//            "Указаны неправильные координаты долготы и широты"
//        } catch (e: Exception) {
//            // return "Problems connecting to the weather service.\nTry again later.";
//            throw RuntimeException(e)
//        }
//        return result
//    }

    fun getForecastByCity(city: String): String {
        val urlString = apiCallTemplate + city + apiKeyTemplate + appid
        val result: String = try {
            val con = getConnectionToForecastApi(urlString)
            val rawJson = getRawJsonFromConnection(con)
            val forecast = Gson().fromJson(rawJson, Forecast::class.java)
            val stringsForecasts = convertRawJsonToListForecasts(rawJson)
            "$city:\n${parseForecastJsonFromList(stringsForecasts)}"
        } catch (e: IllegalArgumentException) {
            "Указано неправильное название города: $city"
        } catch (e: Exception) {
            // return "Problems connecting to the weather service.\nTry again later.";
            throw RuntimeException(e)
        }
        return result
    }

    private fun getRawJsonFromConnection(connection: HttpURLConnection): String {
        val response = StringBuilder()
        try {
            BufferedReader(InputStreamReader(connection.inputStream)).use { bufferedReader ->
                var inputLine: String?
                while (bufferedReader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
            }
        } catch (e: IOException) {
            response.append("Возникла проблема: ").append(e.message)
        }
        return response.toString()
    }

    private fun getConnectionToForecastApi(urlString: String): HttpURLConnection {
        val connection: HttpURLConnection
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

    private fun convertRawJsonToListForecasts(rawJson: String): List<String> {
        var weatherList = mutableListOf<String>()
        val arrNode = ObjectMapper().readTree(rawJson)["list"]
        if (arrNode.isArray) {
            weatherList = arrNode.map { it.toString() }
                .take(17)
                .toMutableList()
        }
        return weatherList
    }

    private fun parseForecastJsonFromList(weatherList: List<String>): String {
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
                    stringBuilder
                        .append("Возникла проблема: ")
                        .append(e.message)
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
        val roundedTemperature = Math.round(temperature)
        val formattedTemperature =
            if (roundedTemperature > 0) "+$roundedTemperature" else roundedTemperature.toString()
        val formattedDescription = description.replace("\"".toRegex(), "")
        val weatherUnicode = convertDescriptionToUnicode(formattedDescription)
        return String.format(
            "%s %5s %-4s %s", formattedDateTime, formattedTemperature,
            weatherUnicode, System.lineSeparator()
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