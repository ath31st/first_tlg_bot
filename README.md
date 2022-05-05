## Welcome to my first bot project. 

The theme of the project is a weather bot with a bit of humor.

#### Project objectives:
Develop a telegram bot using several third-party APIs.
Implement ReplyKeyboardMarkup, logging, auto-reconnection.
Place the bot on the linux(raspbian) server using PM2(advanced, production process manager for node.js)

#### What his can do?
1. give a weather forecast for Moscow
2. give a weather forecast for any city transmitted from the chat
3. send a random joke from the Bash.org

#### List of supported commands:
    /start
    /joke
    /weather

#### List of used libraries:
1. telegram bots - library to create telegram bots
2. telegram bots extensions - extensions bots for telegram bots library
3. lombok - saves us from boilerplate code
4. log4j - logger
5. jackson - for working with json objects (weather API in my project)
6. jsoup - for parsing html sheets (jokes from Bash.org)

You can use its services yourself if it is online - **@no_first_bot** his name.

If you want to use a bot with your token, run it with the parameters (bot_name bot_token appid(key from https://openweathermap.org )).

I didn't rent a server for a bot, but just used raspberries. This is my little production server from improvised means.