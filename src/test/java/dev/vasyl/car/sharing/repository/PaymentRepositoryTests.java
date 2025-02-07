package dev.vasyl.car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import dev.vasyl.car.sharing.model.Payment;
import dev.vasyl.car.sharing.util.TestConstantsUtil;
import dev.vasyl.car.sharing.util.TestPaymentUtil;
import dev.vasyl.car.sharing.util.TestRentalUtil;
import dev.vasyl.car.sharing.util.TestUserUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTests {

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeAll
    private static void beforeAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_CARS);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_USERS);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_RENTALS);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_PAYMENTS);
    }

    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
    }

    @SneakyThrows
    static void executeSqlScript(DataSource dataSource, String dbPath) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(dbPath));
        }
    }

    @Test
    @DisplayName("Verify that returns correct page with payments" +
            " when user id correct and rentals exist in db")
    public void findAllByRentalUserId_shouldReturnPageOfPayments_whenUserIdCorrectAndPaymentsExistInDb() {
        Long userId = TestUserUtil.getFirstCustomer().getId();
        List<Payment> actualPayments = paymentRepository.findAllByRental_UserId(userId,
                TestPaymentUtil.getDefaultPaymentPageable()).getContent();
        List<Payment> expectedPayments = TestPaymentUtil.getListOfTwoPayments();

        assertFalse(actualPayments.isEmpty());
        assertEquals(
                expectedPayments.get(0).getRental().getUser().getId(),
                actualPayments.get(0).getRental().getUser().getId());
    }

    @Test
    @DisplayName("Verify that returns presented optional with payment" +
            " when rental id correct and payment exist in db")
    public void findByRentalId_shouldReturnOptionalPresented_whenRentalIdCorrectAndPaymentExist() {
        Long rentalId = TestRentalUtil.getNotCompletedRental().getId();
        Payment expectedPayment = TestPaymentUtil.getPaymentWithNotCompletedRental();
        Optional<Payment> actualPayment = paymentRepository.findByRentalId(rentalId);

        assertTrue(actualPayment.isPresent());
        assertEquals(3, actualPayment.get().getId());
        assertEquals(expectedPayment.getAmountToPay(), actualPayment.get().getAmountToPay());
        assertEquals(expectedPayment.getType(), actualPayment.get().getType());
        assertEquals(expectedPayment.getStatus(), actualPayment.get().getStatus());
        assertEquals(expectedPayment.getSessionId(), actualPayment.get().getSessionId());
        assertEquals(expectedPayment.getSessionUrl(), actualPayment.get().getSessionUrl());
        assertEquals(3L, actualPayment.get().getRental().getId());
    }
}
