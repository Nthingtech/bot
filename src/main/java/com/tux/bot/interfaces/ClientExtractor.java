package com.tux.bot.interfaces;

import com.tux.bot.model.Client;
import dev.langchain4j.service.UserMessage;

public interface ClientExtractor {

    @UserMessage("Extract information about a client form {{textClient}}")
    Client extractClientFrom(String textClient);
}
