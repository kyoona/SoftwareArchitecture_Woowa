package sa.service;

import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sa.domain.Location;
import sa.domain.UserRole;
import sa.dto.StoreRequestDto;
import sa.dto.UserAddDto;
import sa.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class StoreServiceTest {
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;

    private Long userId, managerId;

    @BeforeEach
    public void setUp() {
        // given
        UserAddDto userAddDto = new UserAddDto("가게1", new Location("loc1", 1.1, 1.1), UserRole.STORE);
        userId = userService.addUser(userAddDto);

        UserAddDto managerAddDto = new UserAddDto("매니저1", new Location("loc2", 10.1, -1.1), UserRole.MANAGER);
        managerId = userService.addUser(managerAddDto);
    }
    @Test
    public void requestStore_validRequest() {
        // given
        StoreRequestDto storeRequestDto = new StoreRequestDto("가게1", new Location("loc1", 1.1, 1.1), 1000, 12000);

        // when
        Long requestId = storeService.requestStore(userId, storeRequestDto);

        // then
        assertThat(requestId).isNotNull();
    }

    @Test
    public void requestStore_invalidRequest() {
        // given
        StoreRequestDto storeRequestDto = new StoreRequestDto("가게1", new Location("loc1", 1.1, 1.1), 1000, 12000);

        assertThatThrownBy(() -> storeService.requestStore(managerId, storeRequestDto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void acceptStore_validRequest() {
        // given
        StoreRequestDto storeRequestDto = new StoreRequestDto("가게1", new Location("loc1", 1.1, 1.1), 1000, 12000);
        Long requestId = storeService.requestStore(userId, storeRequestDto);

        // when
        Long storeId = storeService.acceptStore(managerId, requestId);

        // then
        assertThat(storeId).isNotNull();
    }

    @Test
    public void acceptStore_invalidRequest() {
        // given
        StoreRequestDto storeRequestDto = new StoreRequestDto("가게1", new Location("loc1", 1.1, 1.1), 1000, 12000);
        Long requestId = storeService.requestStore(userId, storeRequestDto);

        assertThatThrownBy(() -> storeService.acceptStore(userId, requestId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void denyStore_validRequest() {
        // given
        StoreRequestDto storeRequestDto = new StoreRequestDto("가게1", new Location("loc1", 1.1, 1.1), 1000, 12000);
        Long requestId = storeService.requestStore(userId, storeRequestDto);

        // when
        Long result = storeService.denyStore(managerId, requestId);

        // then
        assertThat(result).isEqualTo(requestId);
    }

    @Test
    public void denyStore_invalidRequest() {
        // given
        StoreRequestDto storeRequestDto = new StoreRequestDto("가게1", new Location("loc1", 1.1, 1.1), 1000, 12000);
        Long requestId = storeService.requestStore(userId, storeRequestDto);

        assertThatThrownBy(() -> storeService.denyStore(userId, requestId))
                .isInstanceOf(RuntimeException.class);
    }
}
