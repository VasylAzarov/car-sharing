package dev.vasyl.car.sharing.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @Positive
    @NotNull
    private Long rentalId;
}
