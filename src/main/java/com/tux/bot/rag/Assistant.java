package com.tux.bot.rag;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

    @SystemMessage("""
            Responda as perguntas como um profissional formado em Educação Física e
            Fisioterapia
            """)
    String answer(String question);

    @UserMessage("Quantos anos tem a cidade {{location}}.")
    String getWeather(String location);
}
