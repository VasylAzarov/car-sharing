package dev.vasyl.car.sharing.scheduler;

import dev.vasyl.car.sharing.model.Rental;
import dev.vasyl.car.sharing.repository.RentalRepository;
import dev.vasyl.car.sharing.service.impl.AsyncTelegramNotificationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final RentalRepository rentalRepository;
    private final AsyncTelegramNotificationService asyncTelegramNotificationService;

    @Scheduled(cron = "0 0 10 * * *")
    public void runScheduledTaskEachDay10AM() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Rental> overdueRentals = rentalRepository
                .findByReturnDateBeforeAndActualReturnDateIsNull(tomorrow);

        for (Rental rental : overdueRentals) {
            String message = generateOverdueRentalMessage(rental);
            asyncTelegramNotificationService.sendNotification(message);
        }
    }

    private String generateOverdueRentalMessage(Rental rental) {
        return "User with email ["
                + rental.getUser().getEmail()
                + "] has overdue rental for car ["
                + rental.getCar().getBrand()
                + " "
                + rental.getCar().getModel()
                + "], rental dates: "
                + rental.getRentalDate()
                + " -> "
                + rental.getReturnDate();
    }
}
