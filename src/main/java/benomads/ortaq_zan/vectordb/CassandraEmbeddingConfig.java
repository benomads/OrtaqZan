package benomads.ortaq_zan.vectordb;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.cassandra.CassandraEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;


@Configuration
public class CassandraEmbeddingConfig {
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public CassandraEmbeddingStore cassandraEmbeddingStore(){
        String astraToken = "AstraCS:QrxvsbJSRLClgRilRBtYKuxA:91e6343e06fd89eef59a52ebb6de6de5075e0e1e9bf4d4ecdf77a56f72894f5e";
        String databaseId = "82b6d1a4-446c-45e9-a587-2e2c86e53ba3";

        return new CassandraEmbeddingStore.BuilderAstra()
            .databaseId(UUID.fromString(databaseId))
            .databaseRegion("us-east1")
            .token(astraToken)
            .table("ortaq_zan_vector")
            .dimension(384)
            .build();
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        return EmbeddingStoreIngestor.builder()
            .documentSplitter(DocumentSplitters.recursive(300, 0))
            .embeddingModel(embeddingModel())
            .embeddingStore(cassandraEmbeddingStore())
            .build();
    }

    @Bean
    public ConversationalRetrievalChain conversationalRetrievalChain() {
        return ConversationalRetrievalChain.builder()
            .chatLanguageModel(OpenAiChatModel.withApiKey("your-open-api-key"))
            .retriever(EmbeddingStoreRetriever.from(cassandraEmbeddingStore(), embeddingModel()))
            .build();
    }
}
