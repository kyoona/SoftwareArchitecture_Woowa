package sa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class DeliveryManRequestInfo {
    @Column(name = "deliveryManRequestInfoId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String deliveryManName;

    @Embedded
    private Location location;

    @Setter
    @Enumerated(EnumType.STRING)
    private DeliveryManRequestStatus status = DeliveryManRequestStatus.WAIT;

    public static DeliveryManRequestInfo create(String deliveryManName, Location location) {
        DeliveryManRequestInfo deliveryManRequestInfo = new DeliveryManRequestInfo();
        deliveryManRequestInfo.deliveryManName = deliveryManName;
        deliveryManRequestInfo.location = location;
        return deliveryManRequestInfo;
    }

    public DeliveryMan createDeliveryMan() {
        return new DeliveryMan(this.deliveryManName, this.location);
    }
}
