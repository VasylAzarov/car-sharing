package dev.vasyl.car.sharing.service.impl;

import dev.vasyl.car.sharing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncTelegramNotificationService {

    private NotificationService notificationService;

    @Async
    public void sendNotification(String message) {
        notificationService.sendNotification(message);
    }

}
