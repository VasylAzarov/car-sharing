package dev.vasyl.car.sharing.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;

@Data
public class PaymentResponseDto {

    @Schema(description = "Payment id",
            example = "1")
    private Long id;

    @Schema(description = "Payment status",
            example = "PAID")
    private String status;

    @Schema(description = "Payment type",
            example = "PAYMENT")
    private String type;

    @Schema(description = "Rental ID",
            example = "1")
    private Long rentalId;

    @Schema(description = "Payment session URL",
            example = "https://checkout.stripe.com/pay/session_id")
    private URL sessionUrl;

    @Schema(description = "Payment session ID",
            example = "sess_12345")
    private String sessionId;

    @Schema(description = "Amount to pay",
            example = "100.00")
    private BigDecimal amountToPay;
}
