package dev.vasyl.car.sharing.util;

import static dev.vasyl.car.sharing.util.TestRentalUtil.getListOfThreeRentals;

import dev.vasyl.car.sharing.dto.payment.PaymentRequestDto;
import dev.vasyl.car.sharing.dto.payment.PaymentResponseDto;
import dev.vasyl.car.sharing.model.Payment;
import dev.vasyl.car.sharing.model.Rental;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestPaymentUtil {
    private static Payment createPayment(Long id,
                                         Payment.PaymentStatus status,
                                         Payment.PaymentType type,
                                         Rental rental,
                                         BigDecimal amount,
                                         String sessionId) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setStatus(status);
        payment.setType(type);
        payment.setRental(rental);
        payment.setAmountToPay(amount);
        payment.setSessionId(sessionId);
        payment.setDeleted(false);
        try {
            payment.setSessionUrl(URI.create("https://payment-gateway.com/session" + id).toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error creating payment URLs", e);
        }
        return payment;
    }

    public static List<Payment> getListOfTwoPayments() {
        List<Rental> rentalList = getListOfThreeRentals();
        return List.of(
                createPayment(2L,
                        Payment.PaymentStatus.PAID,
                        Payment.PaymentType.PAYMENT,
                        rentalList.get(1),
                        BigDecimal.valueOf(129.99),
                        "sess_67890"),
                createPayment(3L,
                        Payment.PaymentStatus.PENDING,
                        Payment.PaymentType.FINE,
                        rentalList.get(2),
                        BigDecimal.valueOf(59.99),
                        "sess_54321")
        );
    }

    public static PaymentResponseDto getPaymentResponseDto(Payment payment) {
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setId(payment.getId());
        responseDto.setRentalId(payment.getRental().getId());
        responseDto.setStatus(payment.getStatus().toString());
        responseDto.setSessionId(payment.getSessionId());
        responseDto.setSessionUrl(payment.getSessionUrl());
        responseDto.setType(payment.getType().toString());
        responseDto.setAmountToPay(payment.getAmountToPay());
        return responseDto;
    }

    public static String createNotificationForTestSuccessPayment(Payment payment) {
        return "success payment received by user with email ["
                + payment.getRental().getUser().getEmail()
                + "], amount ["
                + payment.getAmountToPay()
                + "]";
    }

    public static Payment getPaymentWithCompletedRental() {
        return getListOfTwoPayments().get(0);
    }

    public static Payment getPaymentWithNotCompletedRental() {
        return getListOfTwoPayments().get(1);
    }

    public static Pageable getDefaultPaymentPageable() {
        return PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "status"));
    }
}
