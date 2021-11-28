package dev.muktiarafi.marisehat.mapper;

import com.microsoft.graph.models.User;
import dev.muktiarafi.marisehat.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.displayName")
    @Mapping(target = "email", source = "user.userPrincipalName")
    UserDto userToUserDto(User user);
}
