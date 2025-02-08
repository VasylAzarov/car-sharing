package dev.vasyl.car.sharing.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vasyl.car.sharing.dto.rental.RentalCreateRequestDto;
import dev.vasyl.car.sharing.dto.rental.RentalResponseDto;
import dev.vasyl.car.sharing.dto.rental.RentalSetActualReturnRequestDto;
import dev.vasyl.car.sharing.model.Rental;
import dev.vasyl.car.sharing.model.User;
import dev.vasyl.car.sharing.util.TestConstantsUtil;
import dev.vasyl.car.sharing.util.TestRentalUtil;
import dev.vasyl.car.sharing.util.TestUserUtil;
import java.sql.Connection;
import javax.sql.DataSource;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        Dotenv dotenv = Dotenv.load();
        registry.add(TestConstantsUtil.JWT_EXPIRATION_VALUE_NAME,
                () -> dotenv.get(TestConstantsUtil.JWT_EXPIRATION_ENV_NAME));
        registry.add(TestConstantsUtil.JWT_SECRET_VALUE_NAME,
                () -> dotenv.get(TestConstantsUtil.JWT_SECRET_ENV_NAME));
        registry.add(TestConstantsUtil.STRIPE_SECRET_VALUE_NAME,
                () -> dotenv.get(TestConstantsUtil.STRIPE_SECRET_ENV_NAME));
        registry.add(TestConstantsUtil.TELEGRAM_SECRET_VALUE_NAME,
                () -> dotenv.get(TestConstantsUtil.TELEGRAM_SECRET_ENV_NAME));
        registry.add(TestConstantsUtil.TELEGRAM_USER_NAME_VALUE_NAME,
                () -> dotenv.get(TestConstantsUtil.TELEGRAM_USER_NAME_ENV_NAME));
        registry.add(TestConstantsUtil.TELEGRAM_CHAT_ID_VALUE_NAME,
                () -> dotenv.get(TestConstantsUtil.TELEGRAM_CHAT_ID_ENV_NAME));
    }

    @BeforeAll
    public static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
    }

    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
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
    public static void executeSqlScript(DataSource dataSource, String sql) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(sql));
        }
    }

    @Test
    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @DisplayName("Validate that create a new rental with valid data")
    public void startRental_ValidRequestDto_Success() throws Exception {
        Rental rental = TestRentalUtil.getNotCompletedRental();
        RentalCreateRequestDto createRentalDto = TestRentalUtil.getRentalCreateRequestDto(rental);
        RentalResponseDto expected = TestRentalUtil.getRentalResponseDto(rental);

        mockMvc.perform(
                post(TestConstantsUtil.RENTAL_CONTROLLER_PATH)
                        .content(objectMapper.writeValueAsString(createRentalDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rentalDate").value(
                        expected.getRentalDate().toString()))
                .andExpect(jsonPath("$.returnDate").value(
                        expected.getReturnDate().toString()))
                .andExpect(jsonPath("$.carId", is(
                        expected.getCarId().intValue())));
    }

    @Test
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @DisplayName("Verify that find all existent rentals")
    public void getAllByParams_WithValidData_Success() throws Exception {
        User user = TestUserUtil.getFirstCustomer();

        mockMvc.perform(
                get(TestConstantsUtil.RENTAL_CONTROLLER_PATH)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "rentalDate")
                        .param("userId", String.valueOf(user.getId()))
                        .param("isActive", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content",
                        hasSize(1)))
                .andExpect(jsonPath("$.content[0].userId",
                        is(user.getId().intValue())));
    }

    @Test
    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @DisplayName("Verify that get existent rental by valid ID")
    public void getById_ValidRentalId_ReturnRentalDto() throws Exception {
        Rental rental = TestRentalUtil.getCompletedRental();

        mockMvc.perform(
                        get(TestConstantsUtil.RENTAL_CONTROLLER_PATH + "/{id}",
                                rental.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualReturnDate")
                        .value(rental.getActualReturnDate().toString()));
    }

    @Test
    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @DisplayName("Verify that complete existing rental by valid id should return RentalResponseDto")
    public void completeRental_ValidData_Success() throws Exception {
        Rental rental = TestRentalUtil.getNotCompletedRental();
        RentalSetActualReturnRequestDto requestDto
                = TestRentalUtil.getRentalSetActualReturnRequestDto(rental);
        Long expectedUserId = TestUserUtil.getSecondCustomer().getId();

        mockMvc.perform(
                        post(TestConstantsUtil.RENTAL_CONTROLLER_PATH + "/return",
                                requestDto.rentalId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId")
                        .value(expectedUserId))
                .andExpect(jsonPath("$.actualReturnDate")
                        .exists());
    }
}
