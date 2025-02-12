package dev.vasyl.car.sharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.vasyl.car.sharing.dto.payment.PaymentResponseDto;
import dev.vasyl.car.sharing.exception.EntityNotFoundException;
import dev.vasyl.car.sharing.mapper.PaymentMapper;
import dev.vasyl.car.sharing.model.Payment;
import dev.vasyl.car.sharing.repository.PaymentRepository;
import dev.vasyl.car.sharing.service.impl.AsyncTelegramNotificationService;
import dev.vasyl.car.sharing.service.impl.StripePaymentService;
import dev.vasyl.car.sharing.service.impl.TelegramNotificationService;
import dev.vasyl.car.sharing.util.TestPaymentUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
public class StripePaymentServiceTests {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private TelegramNotificationService telegramNotificationService;
    @Mock
    private AsyncTelegramNotificationService asyncTelegramNotificationService;

    @InjectMocks
    private StripePaymentService stripePaymentService;

    @Test
    @DisplayName("Should return page of payments for a user")
    void getAllPaymentsByUserId_shouldReturnPageOfPayments() {
        Page<Payment> payments = new PageImpl<>(TestPaymentUtil.getListOfTwoPayments());

        when(paymentRepository.findAllByRental_UserId(1L,
                TestPaymentUtil.getDefaultPaymentPageable()))
                .thenReturn(payments);
        when(paymentMapper.toDtoPage(payments))
                .thenReturn(new PageImpl<>(List.of(TestPaymentUtil.getPaymentResponseDto(
                        TestPaymentUtil.getPaymentWithNotCompletedRental()))));

        Page<PaymentResponseDto> result = stripePaymentService
                .getAllPaymentsByUserId(1L,
                        TestPaymentUtil.getDefaultPaymentPageable());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paymentRepository).findAllByRental_UserId(anyLong(), any());
        verify(paymentMapper).toDtoPage(any());
    }

    @Test
    @DisplayName("Should update payment status to PAID and notify user")
    void verifySuccessfulPayment_shouldUpdatePaymentStatus() {
        Payment pendingPayment = TestPaymentUtil.getPaymentWithNotCompletedRental();
        Payment expectedPayment = TestPaymentUtil.getPaymentWithCompletedRental();
        String expectedNotification =
                TestPaymentUtil.createNotificationForTestSuccessPayment(expectedPayment);

        when(paymentRepository.findById(2L)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(pendingPayment)).thenReturn(expectedPayment);

        stripePaymentService.verifySuccessfulPayment(expectedPayment.getId());

        assertEquals(Payment.PaymentStatus.PAID, expectedPayment.getStatus());
        verify(telegramNotificationService).sendNotification(expectedNotification);
        verify(asyncTelegramNotificationService).sendNotification(expectedNotification);
        verify(paymentRepository).findById(2L);
        verify(paymentRepository).save(pendingPayment);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when payment not found")
    void verifySuccessfulPayment_shouldThrowException_whenPaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> stripePaymentService.verifySuccessfulPayment(1L));
        verify(paymentRepository).findById(1L);
    }
}
