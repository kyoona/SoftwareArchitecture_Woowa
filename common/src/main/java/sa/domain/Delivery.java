package sa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "deliveries")
@Entity
public class Delivery {

    @Column(name = "deliveryId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "storeId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @Setter
    private int deliveryManPriorIdx;
    @Setter
    private int deliveryPrice;

    private Long orderId;

    @Setter
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus = DeliveryStatus.WAIT;

    public static Delivery create(User user, Store store, Long orderId, int deliveryPrice){
        Delivery delivery = new Delivery();
        delivery.user = user;
        delivery.store = store;
        delivery.deliveryManPriorIdx = 0;
        delivery.orderId = orderId;
        delivery.deliveryPrice = deliveryPrice;

        if (deliveryPrice <= 0) {
            throw new IllegalArgumentException("Delivery price must be larger than 0.\nCurrent Value: " + deliveryPrice);
        }

        return delivery;
    }
}
