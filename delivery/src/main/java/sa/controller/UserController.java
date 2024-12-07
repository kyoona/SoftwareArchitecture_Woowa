package sa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sa.dto.SimpleResDto;
import sa.dto.UserAddDto;
import sa.service.UserService;

@RequestMapping("/delivery/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping
    public SimpleResDto addUser(@RequestBody UserAddDto userAddDto){
        Long userId = userService.addUser(userAddDto);

        return new SimpleResDto(userId);
    }
}
