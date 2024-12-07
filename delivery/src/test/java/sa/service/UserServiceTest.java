package sa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sa.domain.Location;
import sa.domain.User;
import sa.domain.UserRole;
import sa.dto.UserAddDto;
import sa.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    void addUser_validUsers() {
        List<UserAddDto> userAddDtos = Arrays.asList(
                new UserAddDto("manager", new Location("loc1", 1.1, 1.1), UserRole.MANAGER),
                new UserAddDto("deliveryman", new Location("loc2", 3.2, 4.5), UserRole.DELIVERYMAN),
                new UserAddDto("store", new Location("loc3", 4.1, 71.1), UserRole.STORE),
                new UserAddDto("customer", new Location("loc4", -20.1, 12.1), UserRole.CUSTOMER)
        );

        userAddDtos.forEach(userAddDto -> {
            Long userId = userService.addUser(userAddDto);
            User user = userRepository.findById(userId).orElse(null);

            assertThat(user).isNotNull();
            assertThat(user.getUserName()).isEqualTo(userAddDto.getUserName());
            assertThat(user.getLocation()).isEqualTo(userAddDto.getLocation());
            assertThat(user.getUserRole()).isEqualTo(userAddDto.getUserRole());
        });
    }

    @Test
    void addUser_invalidUsers() {
        List<UserAddDto> userAddDtos = Arrays.asList(
                new UserAddDto(null, new Location("loc1", 1.1, 1.1), UserRole.MANAGER),
                new UserAddDto("deliveryman", null, UserRole.DELIVERYMAN),
                new UserAddDto("store", new Location("loc3", 4.1, 71.1), null),
                new UserAddDto("customer", new Location("loc4", -20.1, 12.1), UserRole.CUSTOMER)
        );

        userAddDtos.forEach(userAddDto -> {
            try {
                userService.addUser(userAddDto);
            } catch (Exception e) {
                assertThat(e).isInstanceOf(IllegalArgumentException.class);
            }
        });
    }
}