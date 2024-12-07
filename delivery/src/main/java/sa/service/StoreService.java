package sa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sa.domain.*;
import sa.dto.StoreRequestDto;
import sa.repository.StoreRepository;
import sa.repository.StoreRequestInfoRepository;
import sa.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreRequestInfoRepository storeRequestInfoRepository;

    @Transactional
    public Long requestStore(Long userId, StoreRequestDto storeRequestDto) {
        User user = userRepository.findById(userId).orElseThrow();
        checkStore(user);

        StoreRequestInfo storeRequestInfo = StoreRequestInfo.create(storeRequestDto.getMinimumOrderPrice(), storeRequestDto.getDeliveryPrice(), storeRequestDto.getLocation(), storeRequestDto.getStoreName());
        storeRequestInfoRepository.save(storeRequestInfo);

        return storeRequestInfo.getId();
    }

    @Transactional
    public Long acceptStore(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkManager(user);

        StoreRequestInfo storeRequestInfo = storeRequestInfoRepository.findById(requestId).orElseThrow();
        storeRequestInfo.setStatus(StoreRequestStatus.ACCEPT);

        Store store = storeRequestInfo.createStore();
        storeRepository.save(store);

        return store.getId();
    }

    @Transactional
    public Long denyStore(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkManager(user);

        StoreRequestInfo storeRequestInfo = storeRequestInfoRepository.findById(requestId).orElseThrow();
        storeRequestInfo.setStatus(StoreRequestStatus.DENY);

        return requestId;
    }

    private void checkManager(User user){
        if(user.getUserRole() != UserRole.MANAGER){
            throw new RuntimeException();
        }
    }

    private void checkStore(User user){
        if(user.getUserRole() != UserRole.STORE){
            throw new RuntimeException();
        }
    }
}
