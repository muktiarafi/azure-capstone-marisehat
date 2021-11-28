package dev.muktiarafi.marisehat.controller;

import com.microsoft.graph.models.User;
import dev.muktiarafi.marisehat.dto.ResponseDto;
import dev.muktiarafi.marisehat.dto.ResponseListDto;
import dev.muktiarafi.marisehat.dto.UserDto;
import dev.muktiarafi.marisehat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admins")
@AllArgsConstructor
public class AdminController {
    private final UserService userService;

    @DeleteMapping("/users/{userId}")
    public ResponseDto<UserDto> deleteUser(@PathVariable String userId) {
        var user = userService.find(userId);
        userService.delete(userId);

        return ResponseDto.<UserDto>builder()
                .status(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(user)
                .build();
    }

    @GetMapping(value = "/users")
    public ResponseListDto<UserDto> findAllUser() {
        var users = userService.findAll();

        return ResponseListDto.<UserDto>builder()
                .data(users)
                .build();
    }
}
