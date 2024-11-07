package com.tux.bot.controller;

import com.tux.bot.Question;
import dev.langchain4j.model.chat.ChatLanguageModel;
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

    @PostMapping("/answer")
    public String aiBot(@RequestBody Question question) {
        return chatLanguageModel.generate(question.question());
    }

}
