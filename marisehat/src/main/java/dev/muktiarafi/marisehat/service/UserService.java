package dev.muktiarafi.marisehat.service;

import com.microsoft.graph.models.User;
import dev.muktiarafi.marisehat.dto.UserDto;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

public interface UserService {
    User create(UserDto userDto);
    User find(OAuth2AuthorizedClient auth2AuthorizedClient);
}
