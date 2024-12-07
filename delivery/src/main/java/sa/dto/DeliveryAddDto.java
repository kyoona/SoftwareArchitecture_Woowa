package sa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sa.domain.Location;
import sa.domain.Order;
import sa.domain.Store;
import sa.domain.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddDto {
    private User user;
    private Store store;
    private Long orderId;
    private int deliveryPrice;
}
