package dev.vasyl.car.sharing.util;

import dev.vasyl.car.sharing.dto.user.RoleNameRequestDto;
import dev.vasyl.car.sharing.dto.user.UserCreateRequestDto;
import dev.vasyl.car.sharing.dto.user.UserResponseDto;
import dev.vasyl.car.sharing.dto.user.UserUpdateResponseDto;
import dev.vasyl.car.sharing.model.Role;
import dev.vasyl.car.sharing.model.User;
import java.util.Set;

public class TestUserUtil {
    private static User createUser(Long id,
                                   String email,
                                   String firstName,
                                   String lastName,
                                   Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("securepass");
        user.setRoles(Set.of(role));
        user.setDeleted(false);
        return user;
    }

    public static User getFirstCustomer() {
        return createUser(2L,
                "customer1@example.com",
                "Jane",
                "Smith",
                getCustomerRole());
    }

    public static User getSecondCustomer() {
        return createUser(3L,
                "customer2@example.com",
                "Bob", "Brown",
                getCustomerRole());
    }

    public static User getManager() {
        return createUser(1L,
                "manager@example.com",
                "John",
                "Doe",
                getManagerRole());
    }

    public static UserCreateRequestDto getUserCreateRequestDto(User user) {
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto();
        userCreateRequestDto.setEmail(user.getEmail());
        userCreateRequestDto.setPassword(user.getPassword());
        userCreateRequestDto.setConfirmedPassword(user.getPassword());
        userCreateRequestDto.setFirstName(user.getFirstName());
        userCreateRequestDto.setLastName(user.getLastName());

        return userCreateRequestDto;
    }

    public static UserUpdateResponseDto getUserUpdateResponseDto(User user) {
        UserUpdateResponseDto userUpdateResponseDto = new UserUpdateResponseDto();
        userUpdateResponseDto.setId(user.getId());
        userUpdateResponseDto.setEmail(user.getEmail());

        return userUpdateResponseDto;
    }

    public static UserResponseDto getUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());

        return userResponseDto;
    }

    public static Role getManagerRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName(Role.RoleName.MANAGER);
        return role;
    }

    public static Role getCustomerRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);
        return role;
    }

    public static RoleNameRequestDto getRoleNameRequestDto(Role.RoleName roleName) {
        return new RoleNameRequestDto(roleName);
    }
}
