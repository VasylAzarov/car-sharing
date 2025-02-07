package dev.vasyl.car.sharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserResponseDto {
    @Schema(description = "User id",
            example = "1")
    private Long id;

    @Schema(description = "User email",
            example = "user@email.com")
    private String email;

    @Schema(description = "User first name",
            example = "Bob")
    private String firstName;

    @Schema(description = "User last name",
            example = "Jonson")
    private String lastName;
}
