package dev.vasyl.car.sharing.dto.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RentalSetActualReturnRequestDto(
        @Schema(description = "Rental ID",
                example = "1")
        @NotNull(message = "Rental ID must not be null")
        @Positive(message = "Rental ID must be greater than 0")
        Long rentalId
) {
}
