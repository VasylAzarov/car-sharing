package dev.vasyl.car.sharing.dto.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalResponseDto {
    @Schema(description = "Rental start date",
            example = "2025-01-31")
    private LocalDate rentalDate;

    @Schema(description = "Rental end date",
            example = "2025-02-15")
    private LocalDate returnDate;

    @Schema(description = "Rental actual end date",
            example = "2025-02-1")
    private LocalDate actualReturnDate;

    @Schema(description = "Car ID",
            example = "1")
    private Long carId;

    @Schema(description = "User ID",
            example = "1")
    private Long userId;
}
