package sa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sa.domain.Location;
import sa.domain.Store;

@Getter
@AllArgsConstructor
public class StoreResDto {

    private String storeName;
    private Location location;
    private int deliveryPrice;
    private int minimumOrderPrice;

    public StoreResDto(Store store) {
        this.storeName = store.getStoreName();
        this.location = store.getLocation();
        this.deliveryPrice = store.getDeliveryPrice();
        this.minimumOrderPrice = store.getMinimumOrderPrice();
    }
}
