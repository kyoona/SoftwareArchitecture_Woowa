package sa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sa.domain.User;
import sa.dto.UserAddDto;
import sa.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long addUser(UserAddDto userAddDto) {
        User user = User.create(userAddDto.getUserName(), userAddDto.getLocation(), userAddDto.getUserRole());
        userRepository.save(user);

        return user.getId();
    }
}
