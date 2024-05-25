package benomads.ortaq_zan.assistant;

import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    Tokenizer tokenizer() {
        return new OpenAiTokenizer("gpt-4o");
    }

    @Bean
    Assistant assistant(StreamingChatLanguageModel streamingChatLanguageModel,
                        Tokenizer tokenizer
    ) {
        return AiServices.builder(Assistant.class)
            .streamingChatLanguageModel(streamingChatLanguageModel)
            .chatMemoryProvider(memoryId -> TokenWindowChatMemory.builder()
                .id(memoryId)
                .maxTokens(1000, tokenizer)
                .build())
            .build();
    }


}
