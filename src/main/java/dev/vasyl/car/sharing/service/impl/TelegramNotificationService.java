package dev.vasyl.car.sharing.service.impl;

import dev.vasyl.car.sharing.exception.TelegramNotificationException;
import dev.vasyl.car.sharing.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramNotificationService
        extends TelegramLongPollingBot implements NotificationService {

    private final String botUsername;
    private final String chatId;
    private final String botToken;

    public TelegramNotificationService(
            DefaultBotOptions options,
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.chat.id}") String chatId) {
        super(options);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.chatId = chatId;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Async
    @Override
    public void sendNotification(String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramNotificationException(
                    "Error when sending telegram notification to chatId [" + chatId + "]");
        }
    }
}
