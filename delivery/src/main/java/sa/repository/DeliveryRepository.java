package sa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.domain.Delivery;
import sa.domain.DeliveryStatus;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findAllByDeliveryStatus(DeliveryStatus deliveryStatus);
}
