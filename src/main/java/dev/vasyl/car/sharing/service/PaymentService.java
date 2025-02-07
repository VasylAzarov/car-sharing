package dev.vasyl.car.sharing.service;

import dev.vasyl.car.sharing.dto.payment.PaymentRequestDto;
import dev.vasyl.car.sharing.dto.payment.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentResponseDto startPayment(PaymentRequestDto requestDto);

    Page<PaymentResponseDto> getAllPaymentsByUserId(Long userId, Pageable pageable);

    void verifySuccessfulPayment(Long paymentId);
}
