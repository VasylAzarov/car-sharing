package dev.vasyl.car.sharing.service;

import dev.vasyl.car.sharing.dto.user.RoleNameRequestDto;
import dev.vasyl.car.sharing.dto.user.UserCreateRequestDto;
import dev.vasyl.car.sharing.dto.user.UserResponseDto;
import dev.vasyl.car.sharing.dto.user.UserUpdateResponseDto;
import dev.vasyl.car.sharing.exception.RegistrationException;
import dev.vasyl.car.sharing.model.User;

public interface UserService {
    UserResponseDto register(UserCreateRequestDto requestDto)
            throws RegistrationException;

    UserUpdateResponseDto updateRole(Long id, RoleNameRequestDto roleNameRequestDto);

    UserResponseDto getMe();

    UserResponseDto update(UserCreateRequestDto requestDto);

    User getCurrentUser();
}
