package sa.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sa.domain.Location;
import sa.domain.UserRole;
import sa.dto.DeliveryManRequestDto;
import sa.dto.UserAddDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class DeliveryManServiceTest {

    @Autowired
    private DeliveryManService deliveryManService;
    @Autowired
    private UserService userService;

    private Long userId, managerId;

    @BeforeEach
    public void setup() {
        // given
        UserAddDto userAddDto = new UserAddDto("기사1", new Location("loc1", 1.1, 1.1), UserRole.DELIVERYMAN);
        userId = userService.addUser(userAddDto);

        UserAddDto managerAddDto = new UserAddDto("매니저1", new Location("loc2", 10.1, -1.1), UserRole.MANAGER);
        managerId = userService.addUser(managerAddDto);
    }

    @Test
    public void requestDeliveryMan_validRequest() {
        // given
        DeliveryManRequestDto deliveryManRequestDto = new DeliveryManRequestDto("기사1", new Location("loc1", 1.1, 1.1));

        // when
        Long requestId = deliveryManService.requestDeliveryMan(userId, deliveryManRequestDto);

        // then
        assertThat(requestId).isNotNull();
    }

    @Test
    public void requestDeliveryMan_invalidRequest() {
        // given
        DeliveryManRequestDto deliveryManRequestDto = new DeliveryManRequestDto("기사1", new Location("loc1", 1.1, 1.1));

        assertThatThrownBy(() -> deliveryManService.requestDeliveryMan(managerId, deliveryManRequestDto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void acceptDeliveryMan_validRequest() {
        // given
        DeliveryManRequestDto deliveryManRequestDto = new DeliveryManRequestDto("기사1", new Location("loc1", 1.1, 1.1));
        Long requestId = deliveryManService.requestDeliveryMan(userId, deliveryManRequestDto);

        // when
        Long deliveryManId = deliveryManService.acceptDeliveryMan(managerId, requestId);

        // then
        assertThat(deliveryManId).isNotNull();
    }

    @Test
    public void acceptDeliveryMan_invalidRequest() {
        // given
        DeliveryManRequestDto deliveryManRequestDto = new DeliveryManRequestDto("기사1", new Location("loc1", 1.1, 1.1));
        Long requestId = deliveryManService.requestDeliveryMan(userId, deliveryManRequestDto);

        assertThatThrownBy(() -> deliveryManService.acceptDeliveryMan(userId, requestId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void denyDeliveryMan_validRequest() {
        // given
        DeliveryManRequestDto deliveryManRequestDto = new DeliveryManRequestDto("기사1", new Location("loc1", 1.1, 1.1));
        Long requestId = deliveryManService.requestDeliveryMan(userId, deliveryManRequestDto);

        // when
        Long result = deliveryManService.denyDeliveryMan(managerId, requestId);

        // then
        assertThat(result).isEqualTo(requestId);
    }

    @Test
    public void denyDeliveryMan_invalidRequest() {
        // given
        DeliveryManRequestDto deliveryManRequestDto = new DeliveryManRequestDto("기사1", new Location("loc1", 1.1, 1.1));
        Long requestId = deliveryManService.requestDeliveryMan(userId, deliveryManRequestDto);

        assertThatThrownBy(() -> deliveryManService.denyDeliveryMan(userId, requestId))
                .isInstanceOf(RuntimeException.class);
    }
}
