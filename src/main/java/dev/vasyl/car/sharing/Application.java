package dev.vasyl.car.sharing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Value("${payment.callback.domain}")
    private String paymentCallbackDomain;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${telegram.bot.username}")
    private String telegramUsername;

    @Value("${telegram.bot.token}")
    private String telegramToken;

    @Value("${telegram.bot.chat.id}")
    private String telegramChatId;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("CHECK VARS:");
        System.out.println(paymentCallbackDomain);
        System.out.println(stripeSecretKey);
        System.out.println(jwtExpiration);
        System.out.println(jwtSecret);
        System.out.println(telegramUsername);
        System.out.println(telegramToken);
        System.out.println(telegramChatId);
        System.out.println("STOP CHECK VARS");
    }
}
