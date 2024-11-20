package com.tux.bot.controller;

import com.tux.bot.BotStructuredTemplate;
import com.tux.bot.dto.Question;
import com.tux.bot.interfaces.ClientExtractor;
import com.tux.bot.model.Client;
import com.tux.bot.model.repository.ClientRepository;
import com.tux.bot.rag.Assistant;
import com.tux.bot.rag.RAGConfiguration;
import com.tux.bot.service.ChatService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class BotController {

   //Instead of @Autowired
   private final ChatLanguageModel chatLanguageModel;
   private final RAGConfiguration config;
   private final ChatService chatService;
   private final ClientRepository clientRepository;
   public BotController(ChatLanguageModel chatLanguageModel, RAGConfiguration config, ChatService chatService, ClientRepository clientRepository) {
       this.chatLanguageModel = chatLanguageModel;
       this.config = config;
       this.chatService = chatService;
       this.clientRepository = clientRepository;

   }

   private Assistant assistente;


   @Value("${OPENAI_KEY}")
   private String apiKey;

    @PostMapping("/answer")
    public String aiBot(@RequestBody Question question) {
        return chatLanguageModel.generate(question.question());
    }

    @PostMapping("/question-ai-service")
    public String answerAiServer(@RequestBody String location) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .build();
        Assistant assistant = AiServices.create(Assistant.class, model);
        return assistant.answer(location);
    }

    @PostMapping("/answer2")
    public String aiBot2(@RequestBody Question question) {
       OpenAiChatModel customModel = OpenAiChatModel.builder() /*More generic --- ChatLanguageModel customModel = new OpenAiChatModel.OpenAiChatModelBuilder()*/
               .apiKey(apiKey)
               .modelName("gpt-4o")
               .temperature(0.1)
               .build();
       return customModel.generate(question.question());
    }

    @GetMapping("/chat")
    public String model(@RequestParam( value = "message", defaultValue = "Hello Ai World! lol") String message) {
        return chatLanguageModel.generate(message);
    }

    @GetMapping("/training")
    public String makeTraining() {
        var template = new BotStructuredTemplate();
        var trPrompt = new BotStructuredTemplate.PromptDeTreino();
        trPrompt.training = "Ganho de massa muscular";
        trPrompt.level = "Avançado";
        trPrompt.pathology = Arrays.asList("sem restrições");

        Prompt prompt = StructuredPromptProcessor.toPrompt(trPrompt);
        return chatLanguageModel.generate(prompt.text()) ;
    }

    @PostMapping("/image")
    public String generateImage(@RequestBody Question question) {
        try {
            ImageModel imageModel = new OpenAiImageModel.OpenAiImageModelBuilder()
                    .apiKey(apiKey)
                    .modelName("dall-e")
                    .build();
            return imageModel.generate(question.question()).content().url().toURL().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/chatwithrag")
    public String conversarViaRag(@RequestBody Question question) {
        try {
            if (assistente == null) {
                assistente = config.configure();
            }
            return assistente.answer(question.question());
        }
        catch (Exception exception) {
            return "Erro";
        }
    }

    @PostMapping("/persist")
    public void handleMessage(@RequestBody String userMessage) {
        chatService.processUserMessage(userMessage);
    }

    @PostMapping("/extract")
    public Client extractClient(@RequestBody String text) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-2024-08-06")
                .responseFormat("json_schema")
                .strictJsonSchema(true)
                .build();
        ClientExtractor clientExtractor = AiServices.create(ClientExtractor.class, model);
        Client client = clientExtractor.extractClientFrom(text);
        return clientRepository.save(client);
    }


}
