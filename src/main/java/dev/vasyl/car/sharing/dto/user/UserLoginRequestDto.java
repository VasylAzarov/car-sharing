package dev.vasyl.car.sharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Schema(description = "User email",
            example = "user@email.com")
    @NotBlank(message = "Email should not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User password",
            example = "Qwerty12345678$")
    @NotBlank
    private String password;
}
