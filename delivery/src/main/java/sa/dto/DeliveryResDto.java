package sa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sa.domain.*;

@Getter
@AllArgsConstructor
public class DeliveryResDto {
    private Long deliveryId;
    private User user;
    private Store store;
    private Long orderId;
    private int deliveryPrice;
    public DeliveryResDto(Delivery delivery) {
        this.deliveryId = delivery.getId();
        this.user = delivery.getUser();
        this.store = delivery.getStore();
        this.orderId = delivery.getOrderId();
        this.deliveryPrice = delivery.getDeliveryPrice();
    }
}
