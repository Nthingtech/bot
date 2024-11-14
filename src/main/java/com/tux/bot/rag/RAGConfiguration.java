package com.tux.bot.rag;

import ai.djl.util.Utils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RAGConfiguration {

    @Value("${OPENAI_KEY}")
    private String apiKey;

    public Assistant configure() throws Exception{
        List<Document> documents;
        documents = FileSystemDocumentLoader.loadDocuments(toPath("documents/"), glob("*.txt"));

        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o")
                .build();

        Assistant assistente = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(createContentRetriever(documents))
                .build();
        return assistente;
    }

    public ContentRetriever createContentRetriever(List<Document> documents) {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }

    // métodos para apontar para o arquivo em questão
    public PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:"+glob);
    }

    public Path toPath(String caminho) {
        try {
            URL fileUrl = Utils.class.getClassLoader().getResource(caminho);
            return Paths.get(fileUrl.toURI());
        }
        catch (URISyntaxException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public String getFileContent() {
        Resource resource = new ClassPathResource("documents/text1.txt");
        try {
            File file = resource.getFile();
            String conteudo = new String(Files.readAllBytes(file.toPath()));
            return  conteudo;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
