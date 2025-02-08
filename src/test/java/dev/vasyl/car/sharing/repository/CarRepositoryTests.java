package dev.vasyl.car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.vasyl.car.sharing.model.Car;
import dev.vasyl.car.sharing.util.TestCarUtil;
import dev.vasyl.car.sharing.util.TestConstantsUtil;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
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
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositoryTests {

    @Autowired
    private CarRepository carRepository;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_USERS);
    }
    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
    }

    @SneakyThrows
    public static void executeSqlScript(DataSource dataSource, String dbPath) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(dbPath));
        }
    }

    @Test
    @DisplayName("Verify that returns correct page when cars exist in db")
    @Sql(scripts = TestConstantsUtil.CLASSPATH + TestConstantsUtil.DB_PATH_ADD_CARS)
    public void findAll_shouldReturnCarsPage_whenCarsExist() {
        List<Car> actualListOfCars = carRepository.findAll(
                TestCarUtil.getDefaultCarPageable()).getContent();

        assertEquals(TestCarUtil.getListOfThreeCars().size(), actualListOfCars.size());
    }
}
