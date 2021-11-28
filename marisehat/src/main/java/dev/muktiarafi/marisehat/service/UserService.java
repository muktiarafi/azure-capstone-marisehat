package dev.muktiarafi.marisehat.service;

import dev.muktiarafi.marisehat.dto.RegisterUserDto;
import dev.muktiarafi.marisehat.dto.UserDto;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.util.List;

public interface UserService {
    UserDto create(RegisterUserDto registerUserDto);
    UserDto find(String userId);
    UserDto find(OAuth2AuthorizedClient auth2AuthorizedClient);
    List<UserDto> findAll();
    void delete(String userId);
}
