package dev.muktiarafi.marisehat.controller;

import com.microsoft.graph.models.User;
import dev.muktiarafi.marisehat.dto.ResponseDto;
import dev.muktiarafi.marisehat.dto.UserDto;
import dev.muktiarafi.marisehat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<User> create(@Valid @RequestBody UserDto userDto) {
        var user = userService.create(userDto);

        return ResponseDto.<User>builder()
                .status(true)
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(user)
                .build();
    }

    @GetMapping
    public ResponseDto<User> current(@RegisteredOAuth2AuthorizedClient("graph")OAuth2AuthorizedClient auth2AuthorizedClient) {
        var user = userService.find(auth2AuthorizedClient);

        return ResponseDto.<User>builder()
                .status(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(user)
                .build();
    }
}
