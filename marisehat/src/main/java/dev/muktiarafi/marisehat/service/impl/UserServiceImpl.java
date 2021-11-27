package dev.muktiarafi.marisehat.service.impl;

import com.microsoft.graph.models.User;
import dev.muktiarafi.marisehat.dto.UserDto;
import dev.muktiarafi.marisehat.model.MSPasswordProfile;
import dev.muktiarafi.marisehat.model.MSUser;
import dev.muktiarafi.marisehat.service.UserService;
import dev.muktiarafi.marisehat.utils.GraphServiceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final String domainName;
    private final GraphServiceUtils graphServiceUtils;

    public UserServiceImpl(
            @Value("${domain.name}") String domainName,
            GraphServiceUtils graphServiceUtils
    ) {
        this.domainName = domainName;
        this.graphServiceUtils = graphServiceUtils;
    }

    public User create(UserDto userDto) {
        var msPasswordProfile = new MSPasswordProfile(
                false,
                userDto.getPassword()
        );
        var msUser = MSUser.builder()
                .accountEnabled(true)
                .userPrincipalName(String.format("%s@%s", userDto.getNickname(), domainName))
                .displayName(userDto.getName())
                .mailNickname(userDto.getNickname())
                .passwordProfile(msPasswordProfile)
                .build();

        var client = graphServiceUtils.client();

        return client.users().buildRequest().post(msUser);
    }

    public User find(OAuth2AuthorizedClient auth2AuthorizedClient) {
        var client = graphServiceUtils.client(auth2AuthorizedClient);

        return client.me().buildRequest().get();
    }
}
