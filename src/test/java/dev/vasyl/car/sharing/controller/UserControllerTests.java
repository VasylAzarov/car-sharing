package dev.vasyl.car.sharing.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vasyl.car.sharing.dto.user.RoleNameRequestDto;
import dev.vasyl.car.sharing.dto.user.UserCreateRequestDto;
import dev.vasyl.car.sharing.dto.user.UserResponseDto;
import dev.vasyl.car.sharing.dto.user.UserUpdateResponseDto;
import dev.vasyl.car.sharing.model.Role;
import dev.vasyl.car.sharing.model.User;
import dev.vasyl.car.sharing.repository.UserRepository;
import dev.vasyl.car.sharing.util.TestConstantsUtil;
import dev.vasyl.car.sharing.util.TestUserUtil;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerTests {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    public void beforeAll(@Autowired DataSource dataSource) {
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
        executeSqlScript(dataSource, TestConstantsUtil.DB_PATH_ADD_USERS);
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
    @DisplayName("Update user role by ID")
    public void updateUserRole_ValidRequest_Success() throws Exception {
        User user = TestUserUtil.getFirstCustomer();
        Role role = TestUserUtil.getManagerRole();
        RoleNameRequestDto requestDto = TestUserUtil.getRoleNameRequestDto(role.getName());
        UserUpdateResponseDto responseDto = TestUserUtil.getUserUpdateResponseDto(user);

        mockMvc.perform(
                        put(TestConstantsUtil.USER_CONTROLLER_PATH
                                + "/{id}/role", responseDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newRole", is(responseDto.getNewRole())));
    }

    @Test
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @DisplayName("Update user role by incorrect ID")
    public void updateUserRole_InvalidRequest_NotFound() throws Exception {
        Role role = TestUserUtil.getManagerRole();
        RoleNameRequestDto requestDto = TestUserUtil.getRoleNameRequestDto(role.getName());

        mockMvc.perform(
                        put(TestConstantsUtil.USER_CONTROLLER_PATH
                                + "/{id}/role", 99)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",
                        is("Error when update user role: user id not found, id [99]")));
    }

    @Test
    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @DisplayName("Get current user info")
    public void getCurrentUserInfo_ValidToken_Success() throws Exception {
        User user = TestUserUtil.getFirstCustomer();
        UserResponseDto responseDto = TestUserUtil.getUserResponseDto(user);

        mockMvc.perform(
                        get(TestConstantsUtil.USER_CONTROLLER_PATH + "/me")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(responseDto.getEmail())));
    }

    @Test
    @WithMockUser(username = "customer1@example.com", roles = {"CUSTOMER"})
    @DisplayName("Update current user profile")
    public void updateCurrentUserInfo_ValidRequest_Success() throws Exception {
        User user = TestUserUtil.getFirstCustomer();
        UserCreateRequestDto requestDto = TestUserUtil.getUserCreateRequestDto(user);
        UserResponseDto responseDto = TestUserUtil.getUserResponseDto(user);

        mockMvc.perform(
                        patch(TestConstantsUtil.USER_CONTROLLER_PATH + "/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(responseDto.getEmail())));
    }
}
