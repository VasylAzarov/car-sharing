package dev.vasyl.car.sharing.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.vasyl.car.sharing.util.TestConstantsUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthCheckControllerTests {

    protected static MockMvc mockMvc;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
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
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void healthCheck_ReturnsOk() throws Exception {
        mockMvc.perform(
                        get(TestConstantsUtil.HEALTH_CONTROLLER_PATH)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(
                        "Health check passed. Application is running smoothly."));
    }
}
