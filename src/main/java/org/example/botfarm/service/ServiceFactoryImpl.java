package org.example.botfarm.service;

public class ServiceFactoryImpl implements ServiceFactory {

    @Override
    public Service makeService(String nameService) {
        switch (nameService) {
            case "/start":
                return new StartService();
            case "/joke":
                return new JokeService();
            case "/weather":
                return new WeatherService();
            default:
                throw new RuntimeException();
        }
    }
}
