package dev.vasyl.car.sharing.dto.user;

import dev.vasyl.car.sharing.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@ValidPassword(passwordField = "password", confirmPasswordField = "confirmedPassword")
public class UserCreateRequestDto {
    @Schema(description = "User email",
            example = "user@email.com")
    @NotBlank(message = "Email should not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User password",
            example = "Qwerty12345678$")
    @NotBlank
    private String password;

    @Schema(description = "User confirmed password,"
            + " should be similar with password",
            example = "Qwerty12345678$")
    @NotBlank
    private String confirmedPassword;

    @Schema(description = "User first name",
            example = "Bob")
    @NotBlank(message = "First name should not be empty")
    private String firstName;

    @Schema(description = "User last name",
            example = "Jonson")
    @NotBlank(message = "Last name should not be empty")
    private String lastName;
}
