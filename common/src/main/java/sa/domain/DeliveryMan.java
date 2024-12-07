package sa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "deliveryMen")
@Entity
public class DeliveryMan {
    @Column(name = "deliveryManId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String deliveryManName;
    @Embedded
    private Location location;

    public DeliveryMan(String deliveryManName, Location location) {
        this.deliveryManName = deliveryManName;
        this.location = location;
    }
}
