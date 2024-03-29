package org.example.botfarm.service

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.example.botfarm.service.forecast.Forecast
import org.slf4j.LoggerFactory

/**
 * This class provides functionality for retrieving weather forecasts from the OpenWeatherMap API.
 *
 * @property appid The API key used for making requests to OpenWeatherMap.
 */
class WeatherService(private val appid: String) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val apiCallTemplate = "http://api.openweathermap.org/data/2.5/forecast?q="
    private val apiCallTemplateWithLatLon =
        "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&APPID=%s"
    private val apiKeyTemplate = "&units=metric&APPID="
    private val userAgent = "Mozilla/5.0"
    private val inputDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val outputDateTimeFormat =
        DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.forLanguageTag("ru-RU"))

    /**
     * Retrieves a weather forecast for the specified city.
     *
     * @param city The name of the city for which the forecast is requested.
     * @return A formatted weather forecast as a string or an error message if the request is invalid.
     */
    fun getForecast(city: String): String {
        val urlString = apiCallTemplate + city + apiKeyTemplate + appid
        return getPreparedResult(urlString)
    }

    /**
     * Retrieves a weather forecast for the specified latitude and longitude coordinates.
     *
     * @param latitude The latitude coordinate.
     * @param longitude The longitude coordinate.
     * @return A formatted weather forecast as a string or an error message if the request is
     * invalid.
     */
    fun getForecast(latitude: Float, longitude: Float): String {
        val urlString = String.format(apiCallTemplateWithLatLon, latitude, longitude, appid)
        return getPreparedResult(urlString)
    }

    /**
     * Retrieves and prepares a weather forecast result based on the provided URL string.
     *
     * @param urlString The URL string for the weather forecast API request.
     * @return A formatted weather forecast as a string or an error message if there are issues
     * with the request.
     */
    private fun getPreparedResult(urlString: String): String {
        val result: String = try {
            val con = getConnectionToForecastApi(urlString)
            val rawJson = getRawJsonFromConnection(con)
            val forecast = Gson().fromJson(rawJson, Forecast::class.java)
            prepareOutputStringWithForecast(forecast)
        } catch (e: IllegalArgumentException) {
            logger.warn("wrong request: ${urlString.substringAfter("?").substringBefore("&units")}")
            "Указано неправильное название города или координаты"
        } catch (e: Exception) {
            // return "Problems connecting to the weather service.\nTry again later.";
            throw RuntimeException(e)
        }
        return result
    }

    /**
     * Retrieves raw JSON data from the specified HTTP connection.
     *
     * @param connection The HttpURLConnection to the weather forecast API.
     * @return Raw JSON data as a string or an error message if there are issues with the connection.
     */
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
            logger.error(e.message)
            response.append("Возникла проблема: ").append(e.message)
        }
        return response.toString()
    }

    /**
     * Creates and configures an HttpURLConnection to connect to the weather forecast API.
     *
     * @param urlString The URL string for the API request.
     * @return The configured HttpURLConnection or throws an exception if there are issues.
     */
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
            logger.error(e.message)
            throw RuntimeException(e)
        }
        return connection
    }

    /**
     * Prepares the output string with weather forecast data.
     *
     * @param forecast The Forecast object containing weather data.
     * @return A formatted weather forecast string.
     */
    private fun prepareOutputStringWithForecast(forecast: Forecast): String {
        return "${forecast.city?.name} ${forecast.city?.country}\n".plus(
            forecast.list
                .take(17)
                .joinToString(separator = "") {
                    formatForecastData(
                        it.dtTxt,
                        it.weather[0].main,
                        it.main?.temp,
                    )
                },
        )
    }

    /**
     * Formats individual forecast data elements into a single string.
     *
     * @param dateTime The date and time of the forecast.
     * @param description The weather description (e.g., "Clear," "Rain").
     * @param temperature The temperature in Celsius.
     * @return A formatted string with forecast data.
     */
    private fun formatForecastData(
        dateTime: String?,
        description: String?,
        temperature: Double?,
    ): String {
        val forecastDateTime =
            LocalDateTime.parse(dateTime ?: "1970-01-01 00:00:00", inputDateTimeFormat)
        val formattedDateTime = forecastDateTime.format(outputDateTimeFormat)
        val roundedTemperature = Math.round(temperature ?: 0.0)
        val formattedTemperature =
            if (roundedTemperature > 0) "+$roundedTemperature" else roundedTemperature.toString()
        val weatherUnicode = convertDescriptionToUnicode(description ?: "no data")
        return String.format(
            "%s %5s %-4s %s",
            formattedDateTime,
            formattedTemperature,
            weatherUnicode,
            System.lineSeparator(),
        )
    }

    /**
     * Converts a weather description to its Unicode representation (icons).
     *
     * @param description The weather description (e.g., "Clouds," "Snow").
     * @return The Unicode representation of the weather description or the description itself
     * if not found.
     */
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
