package sa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sa.domain.Location;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequestDto {

    private String storeName;
    private Location location;
    private int deliveryPrice;
    private int minimumOrderPrice;
}
