package dev.vasyl.car.sharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.vasyl.car.sharing.model.User;
import dev.vasyl.car.sharing.util.TestConstantsUtil;
import dev.vasyl.car.sharing.util.TestUserUtil;
import java.sql.Connection;
import java.util.Optional;
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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    private static void beforeAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_USERS);
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
    @DisplayName("Verify that method returns true when search by correct email")
    public void existByEmail_returnTrue_whenUserWithSuchEmailExist() {
        assertTrue(userRepository.existsByEmail(TestUserUtil.getFirstCustomer().getEmail()));
    }

    @Test
    @DisplayName("Verify that method returns false when search by incorrect email")
    public void existByEmail_returnFalse_whenUserWithSuchEmailNotExist() {
        assertFalse(userRepository.existsByEmail("incorrect@email"));
    }

    @Test
    @DisplayName("Verify that optional user has entity when search by correct email")
    public void findByEmail_returnOptionalPresented_whenUserWithSuchEmailExist() {
        User expectedUser = TestUserUtil.getFirstCustomer();
        Optional<User> actualOptional = userRepository.findByEmail(expectedUser.getEmail());

        assertTrue(actualOptional.isPresent());
        assertEquals(expectedUser.getId(), actualOptional.get().getId());
        assertEquals(expectedUser.getEmail(), actualOptional.get().getEmail());
        assertEquals(expectedUser.getFirstName(), actualOptional.get().getFirstName());
        assertEquals(expectedUser.getLastName(), actualOptional.get().getLastName());
        assertEquals(expectedUser.getPassword(), actualOptional.get().getPassword());
        assertEquals(expectedUser.isDeleted(), actualOptional.get().isDeleted());
    }

    @Test
    @DisplayName("Verify that optional empty when search by incorrect email")
    public void findByEmail_returnOptionalEmpty_whenUserWithSuchEmailNotExist() {
        assertTrue(userRepository.findByEmail("incorrect@email").isEmpty());
    }
}
