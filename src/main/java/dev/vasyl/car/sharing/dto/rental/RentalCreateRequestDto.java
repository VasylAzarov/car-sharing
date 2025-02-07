package dev.vasyl.car.sharing.dto.rental;

import dev.vasyl.car.sharing.validation.ValidDateFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalCreateRequestDto {
    @Schema(description = "Rental start date",
            example = "2025-01-31")
    @NotNull(message = "Rental date must not be null")
    @PastOrPresent(message = "Rental date must be in the past or present")
    @ValidDateFormat(message = "Rental date must match yyyy-MM-dd format")
    private LocalDate rentalDate;

    @Schema(description = "Rental end date",
            example = "2025-02-15")
    @NotNull(message = "Return date must not be null")
    @FutureOrPresent(message = "Return date must be in the future or present")
    @ValidDateFormat(message = "Return date must match yyyy-MM-dd format")
    private LocalDate returnDate;

    @Schema(description = "Car ID",
            example = "1")
    @NotNull(message = "Car ID must not be null")
    @Positive(message = "Car ID must be greater than 0")
    private Long carId;
}
