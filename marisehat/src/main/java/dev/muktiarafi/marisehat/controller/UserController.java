package dev.muktiarafi.marisehat.controller;

import dev.muktiarafi.marisehat.dto.RegisterUserDto;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseDto<UserDto> create(
            @RequestHeader("x-registration-id") String registrationIdHeader,
            @Valid @RequestBody RegisterUserDto registerUserDto,
            @RequestParam(name = "groupName", defaultValue = "User") String groupName
    ) {
        var user = userService.create(registrationIdHeader, registerUserDto, groupName);

        return ResponseDto.<UserDto>builder()
                .status(true)
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(user)
                .build();
    }

    @GetMapping
    public ResponseDto<UserDto> current(@RegisteredOAuth2AuthorizedClient("graph")OAuth2AuthorizedClient auth2AuthorizedClient) {
        var user = userService.find(auth2AuthorizedClient);

        return ResponseDto.<UserDto>builder()
                .status(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(user)
                .build();
    }
}
