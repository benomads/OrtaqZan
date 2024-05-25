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
            .serverUrl("https://my-deployment-08331c.es.us-central1.gcp.cloud.es.io")
            .userName("elastic")
            .password("idgCIxgG0GeNaj5l1uhPWrps")
            .dimension(768)
            .build();
    }

}
