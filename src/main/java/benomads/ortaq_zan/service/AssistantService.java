package benomads.ortaq_zan.service;


import benomads.ortaq_zan.assistant.Assistant;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class AssistantService {

    private final Assistant assistant;

    public AssistantService(Assistant assistant) {
        this.assistant = assistant;
    }


    public Flux<String> chatWithToken(int memoryId, String userMessage) {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

        assistant.chatWithToken(memoryId, userMessage)
            .onNext(sink::tryEmitNext)
            .onComplete(c -> sink.tryEmitComplete())
            .onError(sink::tryEmitError)
            .start();

        return sink.asFlux();
    }



}