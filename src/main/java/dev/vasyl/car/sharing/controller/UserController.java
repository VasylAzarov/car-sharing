package dev.vasyl.car.sharing.controller;

import dev.vasyl.car.sharing.dto.user.RoleNameRequestDto;
import dev.vasyl.car.sharing.dto.user.UserCreateRequestDto;
import dev.vasyl.car.sharing.dto.user.UserResponseDto;
import dev.vasyl.car.sharing.dto.user.UserUpdateResponseDto;
import dev.vasyl.car.sharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User manager",
        description = "API for managing users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role by id",
            description = "Update user role by id. Available for Manager roles")
    public UserUpdateResponseDto update(@PathVariable Long id,
                                        @RequestBody @Valid RoleNameRequestDto roleNameRequestDto) {
        return userService.updateRole(id, roleNameRequestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    @Operation(summary = "Get current user profile",
            description = "Get current user profile. Available for Manager and Customer roles")
    public UserResponseDto getCurrentUserInfo() {
        return userService.getMe();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/me")
    @Operation(summary = "Update current user profile",
            description = "Update current user profile. Available for Manager and Customer roles")
    public UserResponseDto updateCurrentUserInfo(
            @RequestBody @Valid UserCreateRequestDto userCreateRequestDto) {
        return userService.update(userCreateRequestDto);
    }
}
