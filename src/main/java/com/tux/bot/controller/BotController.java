package com.tux.bot.controller;

import com.tux.bot.Question;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {

   //Instead of @Autowired
   private final ChatLanguageModel chatLanguageModel;
   public BotController(ChatLanguageModel chatLanguageModel) {
       this.chatLanguageModel = chatLanguageModel;
   }

   @Value("${OPENAI_KEY}")
   private String apiKey;

    @PostMapping("/answer")
    public String aiBot(@RequestBody Question question) {
        return chatLanguageModel.generate(question.question());
    }

    @PostMapping("/answer2")
    public String aiBot2(@RequestBody Question question) {
       OpenAiChatModel customModel = OpenAiChatModel.builder() /*More generic --- ChatLanguageModel customModel = new OpenAiChatModel.OpenAiChatModelBuilder()*/
               .apiKey(apiKey)
               .modelName("gpt-4o")
               .temperature(0.3)
               .build();
       return customModel.generate(question.question());
    }

}
