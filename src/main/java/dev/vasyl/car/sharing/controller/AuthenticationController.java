package dev.vasyl.car.sharing.controller;

import dev.vasyl.car.sharing.dto.user.UserCreateRequestDto;
import dev.vasyl.car.sharing.dto.user.UserLoginRequestDto;
import dev.vasyl.car.sharing.dto.user.UserLoginResponseDto;
import dev.vasyl.car.sharing.dto.user.UserResponseDto;
import dev.vasyl.car.sharing.exception.RegistrationException;
import dev.vasyl.car.sharing.security.AuthenticationService;
import dev.vasyl.car.sharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication manager",
        description = "API for managing authentication")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "User Registration",
            description = "User Registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "409", description = "User with email already exists")
    })
    public UserResponseDto register(@RequestBody @Valid UserCreateRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login",
            description = "User Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged in"),
    })
    public UserLoginResponseDto register(@RequestBody @Valid UserLoginRequestDto requestDto)
            throws RegistrationException {
        return authenticationService.login(requestDto);
    }
}
