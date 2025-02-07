package dev.vasyl.car.sharing.service.impl;

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
import dev.vasyl.car.sharing.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserCreateRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("user with email ["
                    + requestDto.getEmail()
                    + "] already exist");
        }

        User user = saveUser(userMapper.toModel(requestDto),
                passwordEncoder.encode(requestDto.getPassword()));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserUpdateResponseDto updateRole(Long id, RoleNameRequestDto roleNameRequestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Error when update user role: user id not found, id ["
                                + id + "]"));

        Role role = roleRepository.findByName(roleNameRequestDto.roleName()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Error when update user role: role name not found, name ["
                                + roleNameRequestDto.roleName() + "]"));

        userMapper.updateUserRole(user, role);
        User updatedUser = userRepository.save(user);
        return userMapper.toUserUpdateResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto getMe() {
        return userMapper.toUserResponse(getCurrentUser());
    }

    @Override
    public UserResponseDto update(UserCreateRequestDto requestDto) {
        User user = getCurrentUser();

        if (!validateUserEmail(requestDto.getEmail())) {
            throw new UserProcessingException("Error when change user email from ["
                    + user.getEmail() + "] to [" + requestDto.getEmail() + "]: already exist");
        }

        userMapper.updateModelFromDto(requestDto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UserProcessingException("User not found"));
        } else {
            throw new UserProcessingException("Error when getting current user");
        }
    }

    private User saveUser(User user, String encodedPassword) {
        user.setPassword(encodedPassword);

        Role role = roleRepository.findByName(Role.RoleName.CUSTOMER).orElseThrow(
                () -> new EntityNotFoundException("Error when set user role: user email ["
                        + user.getRoles().toString() + "]"));

        user.setRoles(Set.of(role));
        userRepository.save(user);
        return user;
    }

    private boolean validateUserEmail(String userEmail) {
        String currantEmail = getCurrentUser().getEmail();
        if (userEmail.equals(currantEmail)) {
            return true;
        }
        return !userRepository.existsByEmail(userEmail);
    }
}
