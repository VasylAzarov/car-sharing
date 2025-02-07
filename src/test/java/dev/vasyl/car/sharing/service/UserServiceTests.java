package dev.vasyl.car.sharing.service;

import static dev.vasyl.car.sharing.util.TestUserUtil.getCustomerRole;
import static dev.vasyl.car.sharing.util.TestUserUtil.getFirstCustomer;
import static dev.vasyl.car.sharing.util.TestUserUtil.getManager;
import static dev.vasyl.car.sharing.util.TestUserUtil.getManagerRole;
import static dev.vasyl.car.sharing.util.TestUserUtil.getRoleNameRequestDto;
import static dev.vasyl.car.sharing.util.TestUserUtil.getUserCreateRequestDto;
import static dev.vasyl.car.sharing.util.TestUserUtil.getUserResponseDto;
import static dev.vasyl.car.sharing.util.TestUserUtil.getUserUpdateResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.vasyl.car.sharing.dto.user.RoleNameRequestDto;
import dev.vasyl.car.sharing.dto.user.UserCreateRequestDto;
import dev.vasyl.car.sharing.dto.user.UserResponseDto;
import dev.vasyl.car.sharing.dto.user.UserUpdateResponseDto;
import dev.vasyl.car.sharing.exception.EntityNotFoundException;
import dev.vasyl.car.sharing.exception.RegistrationException;
import dev.vasyl.car.sharing.exception.UserProcessingException;
import dev.vasyl.car.sharing.mapper.UserMapper;
import dev.vasyl.car.sharing.model.Role;
import dev.vasyl.car.sharing.model.User;
import dev.vasyl.car.sharing.repository.RoleRepository;
import dev.vasyl.car.sharing.repository.UserRepository;
import dev.vasyl.car.sharing.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityContextHolder securityContextHolder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should register user successfully when email does not exist")
    void register_shouldRegisterUser_whenEmailNotExists() throws RegistrationException {
        User user = getFirstCustomer();
        UserCreateRequestDto requestDto = getUserCreateRequestDto(user);
        UserResponseDto responseDto = getUserResponseDto(user);
        Role customerRole = getCustomerRole();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(Role.RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(responseDto);

        UserResponseDto response = userService.register(requestDto);

        assertNotNull(response);
        verify(userRepository).existsByEmail(any(String.class));
        verify(userMapper).toModel(any(UserCreateRequestDto.class));
        verify(passwordEncoder).encode(any(String.class));
        verify(roleRepository).findByName(Role.RoleName.CUSTOMER);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Should throw RegistrationException when email already exists")
    void register_shouldThrowRegistrationException_whenEmailAlreadyExists() {
        User user = getFirstCustomer();
        UserCreateRequestDto userCreateRequestDto = getUserCreateRequestDto(user);

        when(userRepository.existsByEmail(userCreateRequestDto.getEmail())).thenReturn(true);

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userService.register(userCreateRequestDto));

        assertEquals("user with email [" + userCreateRequestDto.getEmail() + "] already exist",
                exception.getMessage());

        verify(userRepository).existsByEmail(any(String.class));
    }

    @Test
    @DisplayName("Should update user role successfully when role and user exist")
    void updateRole_shouldUpdateRole_whenRoleAndUserExist() {
        User user = getFirstCustomer();
        Role role = getManagerRole();
        RoleNameRequestDto roleNameRequestDto = getRoleNameRequestDto(Role.RoleName.MANAGER);
        UserUpdateResponseDto responseDto = getUserUpdateResponseDto(user);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleNameRequestDto.roleName())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        when(userMapper.toUserUpdateResponseDto(any(User.class))).thenReturn(responseDto);

        UserUpdateResponseDto response = userService.updateRole(user.getId(), roleNameRequestDto);

        assertNotNull(response);
        assertEquals(responseDto.getNewRole(), response.getNewRole());
        verify(userRepository).findById(any(Long.class));
        verify(roleRepository).findByName(roleNameRequestDto.roleName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user does not exist")
    void updateRole_shouldThrowEntityNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        RoleNameRequestDto roleNameRequestDto = new RoleNameRequestDto(Role.RoleName.MANAGER);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateRole(userId, roleNameRequestDto));
        assertEquals("Error when update user role: user id not found, id [1]", exception.getMessage());
        verify(userRepository).findById(any(Long.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when role does not exist")
    void updateRole_shouldThrowEntityNotFoundException_whenRoleDoesNotExist() {
        RoleNameRequestDto roleNameRequestDto = getRoleNameRequestDto(Role.RoleName.MANAGER);
        User user = getManager();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleNameRequestDto.roleName())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateRole(user.getId(), roleNameRequestDto));
        assertEquals("Error when update user role: role name not found, name [MANAGER]",
                exception.getMessage());
        verify(userRepository).findById(any(Long.class));
        verify(roleRepository).findByName((roleNameRequestDto.roleName()));
    }

    @Test
    @DisplayName("Should return current user data")
    void getMe_shouldReturnCurrentUserData() {
        User user = getManager();
        user.setEmail("test@example.com");
        UserResponseDto responseDto = getUserResponseDto(user);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(responseDto);

        SecurityContextHolder.setContext(securityContext);

        UserResponseDto response = userService.getMe();

        assertNotNull(response);
        assertEquals(responseDto, response);
        verify(userMapper).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserProcessingException when no current user found")
    void getMe_shouldThrowUserProcessingException_whenNoCurrentUserFound() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        UserProcessingException exception = assertThrows(UserProcessingException.class,
                () -> userService.getMe());

        assertEquals("Error when getting current user", exception.getMessage());
    }
}
