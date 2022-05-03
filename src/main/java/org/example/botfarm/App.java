package org.example.botfarm;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Bot firstBot = new Bot(args[0], args[1]);
        firstBot.botConnect();
    }
}
