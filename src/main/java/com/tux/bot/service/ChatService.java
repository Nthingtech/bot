package com.tux.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tux.bot.model.Client;
import com.tux.bot.model.repository.ClientRepository;
import com.tux.bot.rag.Assistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {



    private final ClientRepository clientRepository;
    private final Assistant assistant;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatService(ClientRepository clientRepository, @Value("${OPENAI_KEY}") String apiKey) {
        this.clientRepository = clientRepository;
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .build())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    public void processUserMessage(String userMessage) {

        String simulatedJsonResponse = createJsonResponse(userMessage);
        try {
            Client client = parSeClientFromResponse(simulatedJsonResponse);
            clientRepository.save(client);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Client parSeClientFromResponse(String response) throws IOException {
        JsonNode rootNode = objectMapper.readTree(response);
        Client client = new Client();
        client.setName(rootNode.get("nome").asText());
        client.setAge(rootNode.get("idade").asInt());
        client.setPhone(rootNode.get("telefone").asText());
        return client;
    }

    private String createJsonResponse(String response) {
        String name = extractName(response);
        int age = extractAge(response);
        String phone = extractPhone(response);
        return String.format("{\"nome\": \"%s\", \"idade\": %d, \"telefone\": \"%s\"}", name, age, phone);
    }


    private String extractName(String response) {
        Pattern pattern = Pattern.compile("nome é ([A-Za-zÀ-ÖØ-öø-ÿ\\s]+)");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1); // Retorna o primeiro grupo capturado (o nome)
        }
        return "Nome Desconhecido"; // Valor padrão caso não encontre o nome
    }

    private int extractAge(String response) {
        Pattern pattern = Pattern.compile("(\\d+) anos");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)); // Converte o grupo capturado para int
        }
        return -1; // Valor padrão caso não encontre a idade
    }

    private String extractPhone(String response) {
        Pattern pattern = Pattern.compile("telefone é ([\\(\\)\\d\\s-]+)");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1); // Retorna o grupo capturado
        }
        return "Telefone Desconhecido"; // Valor padrão caso não encontre o telefone
    }



}
