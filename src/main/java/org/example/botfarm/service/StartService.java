package org.example.botfarm.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class StartService implements Service{
    @Override
    public String getResult() {
        return "Вас приветствует бот First bot. " +
                "Моя задача подсказать вам прогноз погоды на ближайшие сутки и немного развлечь вас. " +
                "Укажите название города в сообщении.";
    }
}
