package com.tux.bot.interfaces;

import com.tux.bot.model.Client;
import dev.langchain4j.service.UserMessage;

public interface ClientExtractor {

    @UserMessage("""
            Extract information about a client form {{textClient}}
            and always generate a different id that is in the database"
           """)
    Client extractClientFrom(String textClient);
}
