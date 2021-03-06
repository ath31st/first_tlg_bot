package org.example.botfarm.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ServiceFactoryImpl implements ServiceFactory {
    private final String WEATHER_APPID;

    @Override
    public Service makeService(String inputText) {
        WeatherService weatherService = new WeatherService(WEATHER_APPID);
        if (inputText == null) {
            return new Service() {
                @Override
                public String getResult() {
                    return "Сообщение не является текстом. Картинки, стикеры и прочее непотребство я еще не умею различать.";
                }
            };
        }
        switch (inputText) {
            case "/start":
                return new Service() {
                    @Override
                    public String getResult() {
                        return "Вас приветствует бот First bot. " +
                                "Моя задача подсказать вам прогноз погоды на ближайшие сутки и немного развлечь вас. " +
                                "Укажите название города в сообщении.";
                    }
                };
            case "/помощь":
                return new Service() {
                    @Override
                    public String getResult() {
                        return "Бот обрабатывает следующие команды: \n" + "/start\n" + "/joke\n" + "/weather\n";
                    }
                };
            case "/joke":
            case "/анекдот с Bashorg.org":
                return new JokeService();
            case "Путин":
            case "путин":
                return new Service() {
                    @Override
                    public String getResult() {
                        return "При чем тут Путин?";
                    }
                };
            case "/weather":
            case "/погода в Москве":
                weatherService.setCity("Москва");
                return weatherService;
            default:
                weatherService.setCity(inputText);
                return weatherService;
        }
    }
}
