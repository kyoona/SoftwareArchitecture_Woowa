package sa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sa.domain.*;
import sa.dto.DeliveryAddDto;
import sa.dto.DeliveryResDto;
import sa.event.DeliveryEventPublisher;
import sa.kafka.DeliveryStatusMsg;
import sa.kafka.KafkaProducer;
import sa.kafka.KafkaTopic;
import sa.repository.DeliveryManRepository;
import sa.repository.DeliveryRepository;
import sa.repository.StoreRepository;
import sa.repository.UserRepository;
import sa.scheduler.DeliveryScheduler;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManRepository deliveryManRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    private final KafkaProducer kafkaProducer;
    private final DeliveryScheduler deliveryScheduler;
    private final DeliveryEventPublisher deliveryEventPublisher;

    @Transactional
    public Long requestDelivery(Long userId, DeliveryAddDto deliveryAddDto) {
        User user = userRepository.findById(userId).orElseThrow();
        checkStore(user);

        Delivery delivery = Delivery.create(deliveryAddDto.getUser(), deliveryAddDto.getStore(), deliveryAddDto.getOrderId(), deliveryAddDto.getDeliveryPrice());
        deliveryRepository.save(delivery);

        sendDeliveryRequest(delivery.getId());

        return delivery.getId();
    }

    @Transactional
    public void cancelDelivery(Long userId, Long deliveryId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkStore(user);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        if (delivery.getDeliveryStatus() != DeliveryStatus.WAIT) {
            throw new RuntimeException();
        }

        delivery.setDeliveryStatus(DeliveryStatus.CANCEL);
    }

    public DeliveryResDto getDelivery(Long userId, Long deliveryId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkStore(user);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        return new DeliveryResDto(delivery);
    }

    public List<DeliveryResDto> getWaitDeliveryList(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkStore(user);

        List<Delivery> deliveryWaitList = deliveryRepository.findAllByDeliveryStatus(DeliveryStatus.WAIT);

        return deliveryWaitList.stream()
                .map(DeliveryResDto::new)
                .toList();
    }

    @Transactional
    public void acceptDelivery(Long userId, Long deliveryId, Long deliveryManId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkDeliveryMan(user);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        checkValidDeliveryMan(delivery, deliveryManId);

        if (delivery.getDeliveryStatus() != DeliveryStatus.WAIT) {
            throw new RuntimeException();
        }

        delivery.setDeliveryStatus(DeliveryStatus.ACCEPT);
        deliveryScheduler.cancel(deliveryId);

        kafkaProducer.sendMessage(KafkaTopic.deliver_status, new DeliveryStatusMsg(delivery.getOrderId(), sa.kafka.DeliveryStatus.START));
    }

    @Transactional
    public void denyDelivery(Long userId, Long deliveryId, Long deliveryManId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkDeliveryMan(user);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        checkValidDeliveryMan(delivery, deliveryManId);

        if (delivery.getDeliveryManPriorIdx() + 1 < deliveryManRepository.findAll().size()) {
            delivery.setDeliveryManPriorIdx(delivery.getDeliveryManPriorIdx() + 1);
        }
        else {
            delivery.setDeliveryManPriorIdx(0);
            delivery.setDeliveryPrice(delivery.getDeliveryPrice() + 100);
        }

        sendDeliveryRequest(deliveryId);
    }

    @Transactional
    public void doneDelivery(Long userId, Long deliveryId, Long deliveryManId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkDeliveryMan(user);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        checkValidDeliveryMan(delivery, deliveryManId);

        delivery.setDeliveryStatus(DeliveryStatus.DONE);
        deliveryScheduler.cancel(deliveryId);

        kafkaProducer.sendMessage(KafkaTopic.deliver_status, new DeliveryStatusMsg(delivery.getOrderId(), sa.kafka.DeliveryStatus.FINISH));
    }

    @Transactional
    public void checkDeliveryAcceptAndCancel(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        if(delivery.getDeliveryStatus().equals(DeliveryStatus.WAIT)) {
            delivery.setDeliveryManPriorIdx(
                    (delivery.getDeliveryManPriorIdx() + 1) % deliveryManRepository.findAll().size()
            );
        }
    }

    private void checkStore(User user){
        if(user.getUserRole() != UserRole.STORE){
            throw new RuntimeException();
        }
    }

    private void checkDeliveryMan(User user){
        if(user.getUserRole() != UserRole.DELIVERYMAN){
            throw new RuntimeException();
        }
    }

    private void checkValidDeliveryMan(Delivery delivery, Long deliveryManId) {
        DeliveryMan deliveryMan = deliveryManRepository.findAll()
                .stream()
                .sorted((a, b) -> {
                    Double distanceA = getDistance(a.getLocation(), delivery.getStore().getLocation());
                    Double distanceB = getDistance(b.getLocation(), delivery.getStore().getLocation());
                    if (Math.abs(distanceA - distanceB) < 1e-9) {
                        return a.getId().compareTo(b.getId());
                    }
                    else {
                        return distanceA.compareTo(distanceB);
                    }
                })
                .toList()
                .get(delivery.getDeliveryManPriorIdx());

        if (!deliveryMan.getId().equals(deliveryManId)) {
            throw new RuntimeException();
        }
    }

    public void sendDeliveryRequest(Long deliveryId) {
        deliveryScheduler.cancel(deliveryId);
        deliveryScheduler.reserve(deliveryId, () -> deliveryEventPublisher.publishDeliveryEvent(deliveryId));
    }

    private Double getDistance(Location loc1, Location loc2) {
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getY() - loc2.getY(), 2));
    }

}