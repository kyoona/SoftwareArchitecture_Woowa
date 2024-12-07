package sa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sa.domain.Store;
import sa.domain.User;

public interface StoreRepository extends JpaRepository<Store, Long> {
}