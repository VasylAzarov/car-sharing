package dev.vasyl.car.sharing.dto.car;

import dev.vasyl.car.sharing.model.Car;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarResponseDto {
    @Schema(description = "Car id",
            example = "1L")
    private Long id;

    @Schema(description = "Car model",
            example = "X5")
    private String model;

    @Schema(description = "Car brand",
            example = "BMW")
    private String brand;

    @Schema(description = "Car type",
            example = "SEDAN")
    private Car.CarType type;

    @Schema(description = "Car inventory",
            example = "8")
    private int inventory;

    @Schema(description = "Car dailyFee",
            example = "12.00")
    private BigDecimal dailyFee;
}
