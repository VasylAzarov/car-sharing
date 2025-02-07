package dev.vasyl.car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.vasyl.car.sharing.model.Rental;
import dev.vasyl.car.sharing.util.TestConstantsUtil;
import dev.vasyl.car.sharing.util.TestRentalUtil;
import dev.vasyl.car.sharing.util.TestUserUtil;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTests {
    @Autowired
    private RentalRepository rentalRepository;

    @BeforeAll
    private static void beforeAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
    }

    @AfterAll
    public static void stop(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
    }

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_CARS);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_USERS);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_RENTALS);
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
    @DisplayName("Verify that returns correct page with not completed rentals" +
            " when user id correct and rentals exist in db")
    public void findAllByUserIdAndActualReturnDateIsNull_shouldReturnRentalPage_whenRentalsByIdExist() {
        Rental rental = TestRentalUtil.getListOfThreeRentals().get(0);
        List<Rental> actualRentalList = rentalRepository
                .findAllByUserIdAndActualReturnDateIsNull(
                        TestRentalUtil.getDefaultRentalPageable(),
                        rental.getUser().getId())
                .getContent();

        assertNotNull(actualRentalList);
        assertEquals(1, actualRentalList.size());
    }

    @Test
    @DisplayName("Verify that returns correct page with completed rentals" +
            " when user id correct and rentals exist in db")
    public void findAllByUserIdAndActualReturnDateIsNotNull_shouldReturnRentalPage_whenRentalsByIdExist() {
        Long userId = TestUserUtil.getSecondCustomer().getId();

        List<Rental> actualRentalList = rentalRepository
                .findAllByUserIdAndActualReturnDateIsNotNull(
                        TestRentalUtil.getDefaultRentalPageable(),
                        userId)
                .getContent();

        assertNotNull(actualRentalList);
        assertEquals(1, actualRentalList.size());
    }

    @Test
    @DisplayName("Verify that returns correct page with not completed rentals" +
            " that have date before chosen and rentals exist in db")
    public void findByReturnDateBeforeAndActualReturnDateIsNull() {
        Rental expectedRental = TestRentalUtil.getCorrectRentalForSearchingByDate();
        List<Rental> actualRentalList = rentalRepository
                .findByReturnDateBeforeAndActualReturnDateIsNull(
                        TestRentalUtil.getLocalDateForSearchingRental());

        assertEquals(1, actualRentalList.size());
        assertEquals(expectedRental.getId(),
                actualRentalList.get(0).getId());
        assertEquals(expectedRental.getRentalDate(),
                actualRentalList.get(0).getRentalDate());
        assertEquals(expectedRental.getReturnDate(),
                actualRentalList.get(0).getReturnDate());
        assertEquals(expectedRental.getActualReturnDate(),
                actualRentalList.get(0).getActualReturnDate());
    }
}
