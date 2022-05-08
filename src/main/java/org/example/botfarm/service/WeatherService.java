package org.example.botfarm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class WeatherService extends Service {
    private final String APPID;
    private String city;
    private final static String API_CALL_TEMPLATE = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private final static String API_KEY_TEMPLATE = "&units=metric&APPID=";
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM-dd HH:mm", Locale.US);
    private HttpURLConnection connection;


    public String getResult() {
        String result;
        try {
            connection = getConnectionToForecastApi(city);
            String rawJson = getRawJsonFromConnection(connection);
            List<String> stringsForecasts = convertRawJsonToListForecasts(rawJson);
            result = String.format("%s:%s%s", city, System.lineSeparator(), parseForecastJsonFromList(stringsForecasts));
        } catch (IllegalArgumentException e) {
            return String.format("Указано неправильное название города (%s)", city);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private String getRawJsonFromConnection(HttpURLConnection connection) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.toString();
    }

    private HttpURLConnection getConnectionToForecastApi(String city) {
        String urlString = API_CALL_TEMPLATE + city + API_KEY_TEMPLATE + APPID;
        try {
            URL urlObject = new URL(urlString);
            connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = connection.getResponseCode();
            if (responseCode == 404) {
                throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private static List<String> convertRawJsonToListForecasts(String rawJson) throws Exception {
        List<String> weatherList = new ArrayList<>();

        JsonNode arrNode = new ObjectMapper().readTree(rawJson).get("list");
        if (arrNode.isArray()) {
            for (JsonNode objNode : arrNode) {
                weatherList.add(objNode.toString());
            }
            weatherList = weatherList.stream().limit(9).collect(Collectors.toList());
        }
        return weatherList;
    }

    private String parseForecastJsonFromList(List<String> weatherList) {
        final StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String line : weatherList) {
            {
                String dateTime;
                JsonNode mainNode;
                JsonNode weatherArrNode;
                try {
                    mainNode = objectMapper.readTree(line).get("main");
                    weatherArrNode = objectMapper.readTree(line).get("weather");
                    for (final JsonNode objNode : weatherArrNode) {
                        dateTime = objectMapper.readTree(line).get("dt_txt").toString();
                        stringBuilder.append(formatForecastData(dateTime, objNode.get("main").toString(), mainNode.get("temp").asDouble()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    private static String formatForecastData(String dateTime, String description, double temperature) {
        LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime.replaceAll("\"", ""), INPUT_DATE_TIME_FORMAT);
        String formattedDateTime = forecastDateTime.format(OUTPUT_DATE_TIME_FORMAT);

        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + Math.round(temperature);
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature));
        }

        String formattedDescription = description.replaceAll("\"", "");

        return String.format("%s  %s %s%s", formattedDateTime, formattedTemperature, formattedDescription, System.lineSeparator());
    }
}
