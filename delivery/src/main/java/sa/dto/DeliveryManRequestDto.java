package sa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sa.domain.Location;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryManRequestDto {
    private String deliveryManName;
    private Location location;
}
