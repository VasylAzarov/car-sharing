package dev.vasyl.car.sharing.dto.user;

import dev.vasyl.car.sharing.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserUpdateResponseDto {
    @Schema(description = "User id",
            example = "1")
    private Long id;

    @Schema(description = "User email",
            example = "user@email.com")
    private String email;

    @Schema(description = "User role",
            example = "MANAGER")
    private Role.RoleName newRole;

}
