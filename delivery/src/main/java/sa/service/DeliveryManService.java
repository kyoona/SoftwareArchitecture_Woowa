package sa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sa.domain.*;
import sa.dto.DeliveryManRequestDto;
import sa.repository.DeliveryManRepository;
import sa.repository.DeliveryManRequestInfoRepository;
import sa.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DeliveryManService {
    private final UserRepository userRepository;
    private final DeliveryManRepository deliveryManRepository;
    private final DeliveryManRequestInfoRepository deliveryManRequestInfoRepository;

    @Transactional
    public Long requestDeliveryMan(Long userId, DeliveryManRequestDto deliveryManRequestDto) {
        User user = userRepository.findById(userId).orElseThrow();
        checkDeliveryMan(user);

        DeliveryManRequestInfo deliveryManRequestInfo = DeliveryManRequestInfo.create(deliveryManRequestDto.getDeliveryManName(), deliveryManRequestDto.getLocation());
        deliveryManRequestInfoRepository.save(deliveryManRequestInfo);

        return deliveryManRequestInfo.getId();
    }

    @Transactional
    public Long acceptDeliveryMan(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkManager(user);

        DeliveryManRequestInfo deliveryManRequestInfo = deliveryManRequestInfoRepository.findById(requestId).orElseThrow();
        deliveryManRequestInfo.setStatus(DeliveryManRequestStatus.ACCEPT);

        DeliveryMan deliveryMan = deliveryManRequestInfo.createDeliveryMan();
        deliveryManRepository.save(deliveryMan);

        return deliveryMan.getId();
    }

    @Transactional
    public Long denyDeliveryMan(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkManager(user);

        DeliveryManRequestInfo deliveryManRequestInfo = deliveryManRequestInfoRepository.findById(requestId).orElseThrow();
        deliveryManRequestInfo.setStatus(DeliveryManRequestStatus.DENY);

        return requestId;
    }

    public void checkManager(User user) {
        if(user.getUserRole() != UserRole.MANAGER) {
            throw new RuntimeException();
        }
    }

    public void checkDeliveryMan(User user) {
        if(user.getUserRole() != UserRole.DELIVERYMAN) {
            throw new RuntimeException();
        }
    }
}
