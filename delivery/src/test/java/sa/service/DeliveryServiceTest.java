package sa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sa.domain.*;
import sa.dto.*;
import sa.repository.DeliveryRepository;
import sa.repository.StoreRepository;
import sa.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class DeliveryServiceTest {
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private DeliveryManService deliveryManService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private UserRepository userRepository;

    private Long customerId1, customerId2, deliveryManId1, deliveryManId2, storeId, managerId;
    private Long getDeliveryManId1, getDeliveryManId2, getStoreId;
    @BeforeEach
    public void setup() {
        // given
        UserAddDto userAddDto;

        userAddDto = new UserAddDto("고객1", new Location("loc4", 2.1, 3.1), UserRole.CUSTOMER);
        customerId1 = userService.addUser(userAddDto);

        userAddDto = new UserAddDto("고객2", new Location("loc5", 4.1, 0.1), UserRole.CUSTOMER);
        customerId2 = userService.addUser(userAddDto);

        userAddDto = new UserAddDto("기사1", new Location("loc1", 5.1, 2.1), UserRole.DELIVERYMAN);
        deliveryManId1 = userService.addUser(userAddDto);

        userAddDto = new UserAddDto("기사2", new Location("loc2", 2.1, 1.5), UserRole.DELIVERYMAN);
        deliveryManId2 = userService.addUser(userAddDto);

        userAddDto = new UserAddDto("가게1", new Location("loc3", 1.1, 1.1), UserRole.STORE);
        storeId = userService.addUser(userAddDto);

        userAddDto = new UserAddDto("매니저1", new Location("loc2", 10.1, -1.1), UserRole.MANAGER);
        managerId = userService.addUser(userAddDto);

        Long deliveryManRequestId1 = deliveryManService.requestDeliveryMan(deliveryManId1, new DeliveryManRequestDto("기사1", new Location("loc1", 5.1, 2.1)));
        Long deliveryManRequestId2 = deliveryManService.requestDeliveryMan(deliveryManId2, new DeliveryManRequestDto("기사2", new Location("loc2", 2.1, 1.5)));
        getDeliveryManId1 = deliveryManService.acceptDeliveryMan(managerId, deliveryManRequestId1);
        getDeliveryManId2 = deliveryManService.acceptDeliveryMan(managerId, deliveryManRequestId2);

        Long storeRequestId1 = storeService.requestStore(storeId, new StoreRequestDto("가게1", new Location("loc3", 1.1, 1.1), 2000, 12000));
        getStoreId = storeService.acceptStore(managerId, storeRequestId1);
    }

    @Test
    public void requestDelivery_validRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);

        // when
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // then
        Long deliveryId2 = deliveryService.getDelivery(storeId, deliveryId).getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId2).orElseThrow();
        assertThat(deliveryId).isNotNull();
        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.WAIT);
    }

    @Test
    public void cancelDelivery_validRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        deliveryService.cancelDelivery(storeId, deliveryId);

        // then
        Long deliveryId2 = deliveryService.getDelivery(storeId, deliveryId).getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId2).orElseThrow();
        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.CANCEL);
    }

    @Test
    public void getDelivery_validRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        DeliveryResDto deliveryResDto = deliveryService.getDelivery(storeId, deliveryId);

        // then
        assertThat(deliveryResDto.getDeliveryId()).isEqualTo(deliveryId);
    }

    @Test
    public void getWaitDeliveryList_validRequest() {
        // given
        User user1 = userRepository.findById(customerId1).orElseThrow();
        User user2 = userRepository.findById(customerId2).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto1 = new DeliveryAddDto(user1, store, 1L, 1000);
        DeliveryAddDto deliveryAddDto2 = new DeliveryAddDto(user2, store, 2L, 2000);

        System.out.println("deliveryAdDto1: " + deliveryAddDto1.getUser().getUserName());
        System.out.println("deliveryAdDto2: " + deliveryAddDto2.getUser().getUserName());

        Long deliveryId1 = deliveryService.requestDelivery(storeId, deliveryAddDto1);
        Long deliveryId2 = deliveryService.requestDelivery(storeId, deliveryAddDto2);

        // when
        List<DeliveryResDto> deliveryResDtoList = deliveryService.getWaitDeliveryList(storeId);

        // then
        assertThat(deliveryResDtoList.get(0).getDeliveryId()).isEqualTo(deliveryId1);
        assertThat(deliveryResDtoList.get(1).getDeliveryId()).isEqualTo(deliveryId2);
    }

    @Test
    public void acceptDelivery_validRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        deliveryService.acceptDelivery(deliveryManId2, deliveryId, getDeliveryManId2);

        // then
        Long deliveryId2 = deliveryService.getDelivery(storeId, deliveryId).getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId2).orElseThrow();
        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.ACCEPT);
    }

    @Test
    public void acceptDelivery_invalidRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        assertThatThrownBy(() -> deliveryService.acceptDelivery(deliveryManId1, deliveryId, getDeliveryManId1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void denyDelivery_validRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        deliveryService.denyDelivery(deliveryManId2, deliveryId, getDeliveryManId2);

        // then
        Long deliveryId2 = deliveryService.getDelivery(storeId, deliveryId).getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId2).orElseThrow();
        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.WAIT);
    }
    @Test
    public void denyAndNextPriorityAccept() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        deliveryService.denyDelivery(deliveryManId2, deliveryId, getDeliveryManId2);

        // then
        Long deliveryId2 = deliveryService.getDelivery(storeId, deliveryId).getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId2).orElseThrow();
        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.WAIT);

        // when
        deliveryService.acceptDelivery(deliveryManId1, deliveryId, getDeliveryManId1);

        // then
        deliveryService.denyDelivery(deliveryManId1, deliveryId, getDeliveryManId1);
        Long deliveryId3 = deliveryService.getDelivery(storeId, deliveryId).getDeliveryId();
        Delivery delivery2 = deliveryRepository.findById(deliveryId3).orElseThrow();
        assertThat(delivery2.getDeliveryStatus()).isEqualTo(DeliveryStatus.ACCEPT);
    }

    @Test
    public void doneDelivery_validRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        deliveryService.acceptDelivery(deliveryManId2, deliveryId, getDeliveryManId2);
        deliveryService.doneDelivery(deliveryManId2, deliveryId, getDeliveryManId2);

        // then
        Long deliveryId2 = deliveryService.getDelivery(storeId, deliveryId).getDeliveryId();
        Delivery delivery = deliveryRepository.findById(deliveryId2).orElseThrow();
        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.DONE);
    }

    @Test
    public void doneDelivery_invalidRequest() {
        // given
        User user = userRepository.findById(customerId1).orElseThrow();
        Store store = storeRepository.findById(getStoreId).orElseThrow();

        DeliveryAddDto deliveryAddDto = new DeliveryAddDto(user, store, 1L, 1000);
        Long deliveryId = deliveryService.requestDelivery(storeId, deliveryAddDto);

        // when
        deliveryService.acceptDelivery(deliveryManId2, deliveryId, getDeliveryManId2);

        // when
        assertThatThrownBy(() -> deliveryService.doneDelivery(deliveryManId1, deliveryId, getDeliveryManId1))
                .isInstanceOf(RuntimeException.class);
    }
}
