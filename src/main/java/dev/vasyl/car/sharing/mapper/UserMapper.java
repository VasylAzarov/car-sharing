package dev.vasyl.car.sharing.mapper;

import dev.vasyl.car.sharing.config.MapperConfig;
import dev.vasyl.car.sharing.dto.user.UserCreateRequestDto;
import dev.vasyl.car.sharing.dto.user.UserResponseDto;
import dev.vasyl.car.sharing.dto.user.UserUpdateResponseDto;
import dev.vasyl.car.sharing.model.Role;
import dev.vasyl.car.sharing.model.User;
import java.util.HashSet;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserCreateRequestDto dto);

    UserResponseDto toUserResponse(User user);

    @Mapping(target = "newRole", ignore = true)
    UserUpdateResponseDto toUserUpdateResponseDto(User user);

    default void updateUserRole(User user, Role newRole) {
        user.setRoles(new HashSet<>(Set.of(newRole)));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateModelFromDto(UserCreateRequestDto requestDto, @MappingTarget User user);

    UserResponseDto toDto(User updatedUser);
}
