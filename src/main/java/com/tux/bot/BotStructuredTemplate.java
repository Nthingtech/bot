package com.tux.bot;

import dev.langchain4j.model.input.structured.StructuredPrompt;

import java.util.List;

public class BotStructuredTemplate {

    @StructuredPrompt(
            {
            "Crie um treino para o objetivo {{training}} usando exercícios para o tratamento desta {{pathology}} e tem este {{level}}",
            "Estruture sua resposta da seguinte forma:",
            "Nome do Treino: ...",
            "Descrição do Treino: ...",
            "Duração do Treino: ...",
            "Lista de Exercícios;",
            "-Exercício: ...",
            "-Séries do Exercício: ... X Repetições ou Tempo do Exercício: ... -> Intervalo entre as séries: ...",
            "-Modo de executar o Exercício: ..."
    })
    public static class PromptDeTreino{
        public String training;
        public String level;
        public List<String> pathology;
    }
}
