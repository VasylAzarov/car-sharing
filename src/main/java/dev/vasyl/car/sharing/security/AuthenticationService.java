package dev.vasyl.car.sharing.security;

import dev.vasyl.car.sharing.dto.user.UserLoginRequestDto;
import dev.vasyl.car.sharing.dto.user.UserLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(requestDto.getEmail());
        String token = jwtUtil.generateToken(userDetails.getUsername());
        return new UserLoginResponseDto(token);
    }
}
