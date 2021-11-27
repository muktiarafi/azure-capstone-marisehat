package dev.muktiarafi.marisehat.service.impl;

import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import dev.muktiarafi.marisehat.dto.UserDto;
import dev.muktiarafi.marisehat.model.MSPasswordProfile;
import dev.muktiarafi.marisehat.model.MSUser;
import dev.muktiarafi.marisehat.service.UserService;
import dev.muktiarafi.marisehat.utils.GraphServiceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedList;
import java.util.Optional;

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
        var group = findUserGroup()
                .orElseThrow(() -> new RuntimeException("Group not found"));
        assignUserToGroup(msUser.id, group.id);

        var client = graphServiceUtils.client();

        return client.users().buildRequest().post(msUser);
    }

    public User find(String userId) {
        var client = graphServiceUtils.client();

        return client.users(userId).buildRequest().get();
    }

    @Override
    public User find(OAuth2AuthorizedClient auth2AuthorizedClient) {
        var client = graphServiceUtils.client(auth2AuthorizedClient);

        return client.me().buildRequest().get();
    }

    @Override
    public List<User> findAll() {
        var client = graphServiceUtils.client();
        var users = client.users().buildRequest().get();

        return users.getCurrentPage();
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

    private void assignUserToGroup(String userId, String groupId) {
        var directoryObject = new DirectoryObject();
        directoryObject.id = userId;

        graphServiceUtils.client()
                .groups(groupId).members().references()
                .buildRequest()
                .post(directoryObject);
    }
}
