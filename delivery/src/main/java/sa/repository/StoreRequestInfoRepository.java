package sa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.domain.StoreRequestInfo;

public interface StoreRequestInfoRepository extends JpaRepository<StoreRequestInfo, Long> {
}