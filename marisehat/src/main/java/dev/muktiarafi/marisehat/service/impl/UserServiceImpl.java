package dev.muktiarafi.marisehat.service.impl;

import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.requests.GraphServiceClient;
import dev.muktiarafi.marisehat.dto.RegisterUserDto;
import dev.muktiarafi.marisehat.dto.UserDto;
import dev.muktiarafi.marisehat.mapper.UserMapper;
import dev.muktiarafi.marisehat.model.MSPasswordProfile;
import dev.muktiarafi.marisehat.model.MSUser;
import dev.muktiarafi.marisehat.service.UserService;
import dev.muktiarafi.marisehat.utils.GraphServiceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final String domainName;
    private final GraphServiceUtils graphServiceUtils;
    private final UserMapper userMapper;

    public UserServiceImpl(
            @Value("${domain.name}") String domainName,
            GraphServiceUtils graphServiceUtils,
            UserMapper userMapper
    ) {
        this.domainName = domainName;
        this.graphServiceUtils = graphServiceUtils;
        this.userMapper = userMapper;
    }

    public UserDto create(RegisterUserDto registerUserDto) {
        var msPasswordProfile = new MSPasswordProfile(
                false,
                registerUserDto.getPassword()
        );
        var msUser = MSUser.builder()
                .accountEnabled(true)
                .userPrincipalName(String.format("%s@%s", registerUserDto.getNickname(), domainName))
                .displayName(registerUserDto.getName())
                .mailNickname(registerUserDto.getNickname())
                .passwordProfile(msPasswordProfile)
                .build();

        var client = graphServiceUtils.client();
        var actualUser = client.users().buildRequest().post(msUser);
        var group = findUserGroup()
                .orElseThrow(() -> new RuntimeException("Group not found"));
        assignUserToGroup(actualUser.id, group.id, client);

        return userMapper.userToUserDto(actualUser);
    }

    public UserDto find(String userId) {
        var client = graphServiceUtils.client();
        var user = client.users(userId).buildRequest().get();

        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDto find(OAuth2AuthorizedClient auth2AuthorizedClient) {
        var client = graphServiceUtils.client(auth2AuthorizedClient);
        var user = client.me().buildRequest().get();

        return userMapper.userToUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        var client = graphServiceUtils.client();
        var users = client.users().buildRequest().get();

        return users.getCurrentPage().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String userId) {
        var client = graphServiceUtils.client();

        client.users(userId).buildRequest().delete();
    }

    private Optional<Group> findUserGroup() {
        LinkedList<Option> requestOptions = new LinkedList<>();
        requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
        var client = graphServiceUtils.client();
        var groups = client
                .groups()
                .buildRequest(requestOptions)
                .filter("displayName eq 'User'")
                .get();
        if (groups == null) {
            return Optional.empty();
        }

        return Optional.of(groups.getCurrentPage().get(0));
    }

    private void assignUserToGroup(String userId, String groupId, GraphServiceClient client) {
        var directoryObject = new DirectoryObject();
        directoryObject.id = userId;

        client.groups(groupId).members().references()
                .buildRequest()
                .post(directoryObject);
    }
}
