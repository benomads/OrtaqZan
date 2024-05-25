package benomads.ortaq_zan.assistant;

import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;
import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;


@Configuration
public class AssistantConfig {


    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

//    @Bean
//    EmbeddingStore<TextSegment> embeddingStore() {
//        return new InMemoryEmbeddingStore<>();
//    }


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
            var loadedDocuments = loadDocuments(resource.getFile().getPath(), new TextDocumentParser());
            var ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(recursive(70, 0, tokenizer))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
            ingestor.ingest(loadedDocuments);
//            TextSegment segment1 = TextSegment.from("I like football.");
//            Embedding embedding1 = embeddingModel.embed((TextSegment) loadedDocuments).content();
//            embeddingStore.add(embedding1, (TextSegment) loadedDocuments);
//
//            Thread.sleep(1000);
//
//            Embedding queryEmbedding = embeddingModel.embed("Сколько коз было взято во время написание Конституции и где это было, говорится почему??").content();
//            List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
//            EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);
//
//            System.out.println(embeddingMatch.score()); // 0.81442887
//            System.out.println(embeddingMatch.embedded().text()); // I like football.
        };


    }

    @Bean
    ChatMemoryProvider chatMemoryProvider(Tokenizer tokenizer) {
        // Tokenizer is provided by langchain4j-open-ai-spring-boot-starter
        return chatId -> TokenWindowChatMemory.withMaxTokens(1000, tokenizer);
    }



}
