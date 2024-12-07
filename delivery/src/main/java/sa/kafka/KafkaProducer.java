package sa.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaTopic topic, Object message){
        try {
            String messageStr = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic.name(), messageStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("kafka produce 실패");
        }
    }
}
