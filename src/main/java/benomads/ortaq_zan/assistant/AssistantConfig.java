package benomads.ortaq_zan.assistant;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParserFactory;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;
import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Configuration
public class AssistantConfig {


    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }




    @Bean
    ContentRetriever retriever(EmbeddingModel embeddingModel,
                               EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(1)
            .minScore(0.6)
            .build();
    }

   @Bean
    CommandLineRunner ingestDocsForLangChain(EmbeddingModel embeddingModel,
                                             EmbeddingStore<TextSegment> embeddingStore,
                                             Tokenizer tokenizer,
                                             ResourceLoader resourceLoader) {
        return args -> {
            var resource = resourceLoader.getResource("classpath:data");
            List<Document> loadedDocuments = loadDocuments(resource.getFile().toPath(), new ApacheTikaDocumentParser());
            var ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(recursive(50, 0, tokenizer))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

            ingestor.ingest(loadedDocuments);
        };


    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = AssistantConfig.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider(Tokenizer tokenizer) {
        // Tokenizer is provided by langchain4j-open-ai-spring-boot-starter
        return chatId -> TokenWindowChatMemory.withMaxTokens(1000, tokenizer);
    }



}
