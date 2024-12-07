package sa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.domain.DeliveryMan;

public interface DeliveryManRepository extends JpaRepository<DeliveryMan, Long> {
}
