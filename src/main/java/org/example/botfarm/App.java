package org.example.botfarm;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        // 1. bot name; 2. bot token; 3. appid from openweathermap.org.
        Bot firstBot = new Bot(args[0], args[1], args[2]);
        firstBot.botConnect();
    }
}
