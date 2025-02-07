package dev.vasyl.car.sharing.util;

public class TestConstantsUtil {
    public static final String CLASSPATH = "classpath:";

    // JWT
    public static final String JWT_EXPIRATION_ENV_NAME = "JWT_EXPIRATION";
    public static final String JWT_SECRET_ENV_NAME = "JWT_SECRET";
    public static final String JWT_EXPIRATION_VALUE_NAME = "jwt.expiration";
    public static final String JWT_SECRET_VALUE_NAME = "jwt.secret";

    // Stripe
    public static String STRIPE_SECRET_ENV_NAME = "JWT_SECRET";
    public static String STRIPE_SECRET_VALUE_NAME = "stripe.secret.key";

    // Telegram
    public static String TELEGRAM_SECRET_ENV_NAME = "TELEGRAM_TOKEN";
    public static String TELEGRAM_SECRET_VALUE_NAME = "telegram.bot.token";
    public static String TELEGRAM_USER_NAME_ENV_NAME = "TELEGRAM_USERNAME";
    public static String TELEGRAM_USER_NAME_VALUE_NAME = "telegram.bot.username";
    public static String TELEGRAM_CHAT_ID_ENV_NAME = "TELEGRAM_CHAT_ID";
    public static String TELEGRAM_CHAT_ID_VALUE_NAME = "telegram.bot.chat.id";

    // Database paths
    public static final String DB_PATH_ADD_CARS = "database/car/insert-cars.sql";
    public static final String DB_PATH_ADD_PAYMENTS = "database/payment/insert-payments.sql";
    public static final String DB_PATH_ADD_RENTALS = "database/rental/insert-rentals.sql";
    public static final String DB_PATH_ADD_USERS = "database/user/insert-users.sql";
    public static final String DB_PATH_CLEAR_ALL = "database/clear-all-test-data.sql";

    // Api paths
    public static final String CAR_CONTROLLER_PATH = "/cars";
    public static final String RENTAL_CONTROLLER_PATH = "/rentals";
    public static final String HEALTH_CONTROLLER_PATH = "/health";
    public static final String USER_CONTROLLER_PATH = "/users";

}
