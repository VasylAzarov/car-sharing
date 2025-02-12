package dev.vasyl.car.sharing.dto.user;

import dev.vasyl.car.sharing.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RoleNameRequestDto(
        @NotNull
        @Schema(description = "User newRoleName",
                example = "MANAGER | CUSTOMER")
        Role.RoleName roleName
) {
}
