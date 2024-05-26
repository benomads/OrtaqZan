package benomads.ortaq_zan.vectordb;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticEmbeddingStoreConfig {

    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        return ElasticsearchEmbeddingStore.builder()
            .serverUrl(System.getenv("ELASTICSEARCH_SERVER_URL"))
            .userName("elastic")
            .password(System.getenv("ELASTICSEARCH_SERVER_PASSWORD"))
            .dimension(368)
            .build();
    }

}
