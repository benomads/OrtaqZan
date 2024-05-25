package benomads.ortaq_zan.assistant;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class AssistantConfig {

    @Bean
    StreamingChatLanguageModel streamingChatLanguageModel(@Value("${OPEN_AI_API_KEY}") String apiKey) {
        return OpenAiStreamingChatModel.builder()
            .modelName("gpt-4o")
            .apiKey(apiKey)
            .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    Retriever<TextSegment> retriever(EmbeddingModel embeddingModel,
                                     EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreRetriever.from(
            embeddingStore,
            embeddingModel,
            1,
            0.3
        );
    }

    @Bean
    CommandLineRunner docsToEmbedding(EmbeddingModel embeddingModel,
                                     EmbeddingStore<TextSegment> embeddingStore,
                                     Tokenizer tokenizer,
                                     ResourceLoader resourceLoader) {
        return args -> {
            var resource = resourceLoader.getResource("classpath:data/some-test-2.txt");
            var loadDocument = loadDocument(resource.getFile().toPath());
            var splitter = DocumentSplitters.recursive(100,
                                                        0, tokenizer);
            var ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .documentSplitter(splitter)
                .build();

            ingestor.ingest(loadDocument);
        };
    }

    @Bean
    Tokenizer tokenizer() {
        return new OpenAiTokenizer("gpt-4o");
    }

    @Bean
    Assistant assistant(StreamingChatLanguageModel streamingChatLanguageModel,
                        Tokenizer tokenizer,
                        Retriever<TextSegment> retriever) {

        return AiServices.builder(Assistant.class)
            .streamingChatLanguageModel(streamingChatLanguageModel)
            .chatMemoryProvider(memoryId -> TokenWindowChatMemory.builder()
                .id(memoryId)
                .maxTokens(1000, tokenizer)
                .build())
            .retriever(retriever)
            .build();
    }


}
