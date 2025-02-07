package dev.vasyl.car.sharing.controller;

import dev.vasyl.car.sharing.dto.payment.PaymentRequestDto;
import dev.vasyl.car.sharing.dto.payment.PaymentResponseDto;
import dev.vasyl.car.sharing.service.impl.StripePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment management", description = "Endpoints for managing payments")
public class PaymentController {
    private final StripePaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new payment",
            description = "Create a new payment")
    public PaymentResponseDto createPayment(@RequestBody @Valid
                                                    PaymentRequestDto requestDto) {
        return paymentService.startPayment(requestDto);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get all user's payments",
            description = "Get all user's payments by user ID")
    public Page<PaymentResponseDto> getAllPaymentsByUserId(
            @ParameterObject
            @PageableDefault(size = 20, sort = "status",
                    direction = Sort.Direction.ASC)
                    Pageable pageable,
            @PathVariable Long userId) {
        return paymentService.getAllPaymentsByUserId(userId, pageable);
    }

    @GetMapping("/success/{paymentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Successful page for redirection",
            description = "Successful page for redirection")
    public String paymentSuccessRedirect(@PathVariable Long paymentId) {
        paymentService.verifySuccessfulPayment(paymentId);
        return "Success page";
    }

    @GetMapping("/cancel/{paymentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Cancel page for redirection",
            description = "Cancel page for redirection")
    public String paymentCancelRedirect(@PathVariable Long paymentId) {
        return "Payment with id " + paymentId
                + " was canceled. You can finish it in 24 hours.";
    }
}
