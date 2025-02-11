package dev.vasyl.car.sharing.dto.car;

import dev.vasyl.car.sharing.model.Car;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarCreateUpdateRequestDto {
    @Schema(description = "Car model",
            example = "X5")
    @NotBlank(message = "Model should not be empty")
    private String model;

    @Schema(description = "Car brand",
            example = "BMW")
    @NotBlank(message = "Brand should not be empty")
    private String brand;

    @Schema(description = "Car type",
            example = "SEDAN")
    @NotNull(message = "Type should not be empty")
    private Car.CarType type;

    @Schema(description = "Car inventory",
            example = "8")
    @Min(value = 0, message = "Inventory should not be less than 0")
    private int inventory;

    @Schema(description = "Car dailyFee",
            example = "12.00")
    @NotNull(message = "DailyFee should not be empty")
    @DecimalMin(value = "0.00", message = "DailyFee should not be less than 0")
    private BigDecimal dailyFee;
}
