package sa.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeliveryEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishDeliveryEvent(Long deliveryId){
        DeliveryEvent event = new DeliveryEvent(deliveryId);
        publisher.publishEvent(event);
    }
}
