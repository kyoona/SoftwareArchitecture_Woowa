package sa.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sa.service.DeliveryService;

@RequiredArgsConstructor
@Component
public class DeliveryEventHandler {

    private final DeliveryService deliveryService;

    @EventListener
    public void checkDeliveryAccept(DeliveryEvent deliveryEvent){
        deliveryService.checkDeliveryAcceptAndCancel(deliveryEvent.getDeliveryId());
    }
}
