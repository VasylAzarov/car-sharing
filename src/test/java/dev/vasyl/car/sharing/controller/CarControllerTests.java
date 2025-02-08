package dev.vasyl.car.sharing.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vasyl.car.sharing.dto.car.CarCreateUpdateRequestDto;
import dev.vasyl.car.sharing.dto.car.CarResponseDto;
import dev.vasyl.car.sharing.model.Car;
import dev.vasyl.car.sharing.util.TestCarUtil;
import dev.vasyl.car.sharing.util.TestConstantsUtil;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
    }

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_CARS);
    }

    @AfterEach
    public void afterEach(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_CLEAR_ALL);
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
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @DisplayName("Validate that create a new car with valid data")
    public void saveCar_ValidRequestDto_Success() throws Exception {
        Car car = TestCarUtil.getFirstCar();
        CarCreateUpdateRequestDto createCarDto = TestCarUtil.getCarCreateUpdateRequestDto(car);
        CarResponseDto expected = TestCarUtil.getCarResponseDto(car);

        mockMvc.perform(post(TestConstantsUtil.CAR_CONTROLLER_PATH)
                        .content(objectMapper.writeValueAsString(createCarDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.model").value(expected.getModel()))
                .andExpect(jsonPath("$.brand").value(expected.getBrand()))
                .andExpect(jsonPath("$.type").value(expected.getType().toString()))
                .andExpect(jsonPath("$.inventory", is(expected.getInventory())))
                .andExpect(jsonPath("$.dailyFee", is(expected.getDailyFee().doubleValue())));
    }

    @Test
    @DisplayName("Verify that find all existent cars")
    public void getAllCars_WithValidData_Success() throws Exception {
        mockMvc.perform(get(TestConstantsUtil.CAR_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @DisplayName("Verify that get existent car by valid ID")
    public void getCarById_ValidCarId_ReturnCarDto() throws Exception {
        Car car = TestCarUtil.getLastCar();

        mockMvc.perform(
                        get(TestConstantsUtil.CAR_CONTROLLER_PATH + "/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value(car.getModel()))
                .andExpect(jsonPath("$.brand").value(car.getBrand()))
                .andExpect(jsonPath("$.type").value(car.getType().toString()));
    }

    @Test
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @DisplayName("Verify that update existing car by valid id and new car data, should return carDto")
    public void updateCarById_ValidData_Success() throws Exception {
        Car car = TestCarUtil.getLastCar();
        CarCreateUpdateRequestDto updateDto = TestCarUtil.getCarCreateUpdateRequestDto(car);

        mockMvc.perform(
                        put(TestConstantsUtil.CAR_CONTROLLER_PATH + "/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model", is(updateDto.getModel())))
                .andExpect(jsonPath("$.brand", is(updateDto.getBrand())))
                .andExpect(jsonPath("$.type", is(updateDto.getType().toString())))
                .andExpect(jsonPath("$.inventory", is(updateDto.getInventory())));
    }

    @Test
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @DisplayName("Verify that delete existent car by valid ID")
    public void deleteCar_ValidCarId_Success() throws Exception {
        Car car = TestCarUtil.getFirstCar();

        mockMvc.perform(delete(TestConstantsUtil.CAR_CONTROLLER_PATH + "/{id}", car.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
