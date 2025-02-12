package dev.vasyl.car.sharing.util;

import dev.vasyl.car.sharing.dto.rental.RentalCreateRequestDto;
import dev.vasyl.car.sharing.dto.rental.RentalResponseDto;
import dev.vasyl.car.sharing.dto.rental.RentalSetActualReturnRequestDto;
import dev.vasyl.car.sharing.model.Car;
import dev.vasyl.car.sharing.model.Rental;
import dev.vasyl.car.sharing.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestRentalUtil {
    private static Rental createRental(Long id,
                                       LocalDate rentalDate,
                                       LocalDate returnDate,
                                       LocalDate actualReturnDate,
                                       Car car,
                                       User user) {
        Rental rental = new Rental();
        rental.setId(id);
        rental.setRentalDate(rentalDate);
        rental.setReturnDate(returnDate);
        rental.setActualReturnDate(actualReturnDate);
        rental.setCar(car);
        rental.setUser(user);
        rental.setDeleted(false);
        return rental;
    }

    public static List<Rental> getListOfThreeRentals() {
        List<Car> carList = TestCarUtil.getListOfThreeCars();
        return List.of(
                createRental(3L,
                        LocalDate.of(2024, 3, 1),
                        LocalDate.of(2024, 3, 7),
                        null,
                        carList.get(2),
                        TestUserUtil.getFirstCustomer()),
                createRental(2L,
                        LocalDate.of(2024, 2, 1),
                        LocalDate.of(2024, 2, 5),
                        LocalDate.of(2024, 2, 5),
                        carList.get(1),
                        TestUserUtil.getFirstCustomer()),
                createRental(1L,
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 10),
                        null,
                        carList.get(0),
                        TestUserUtil.getSecondCustomer())
        );
    }

    public static Rental getCompletedRental() {
        return getListOfThreeRentals().get(1);
    }

    public static Rental getCorrectRentalForSearchingByDate() {
        return getListOfThreeRentals().get(2);
    }

    public static Rental getNotCompletedRental() {
        return getListOfThreeRentals().get(0);
    }

    public static LocalDate getLocalDateForSearchingRental() {
        return LocalDate.of(2024, 2, 1);
    }

    public static RentalCreateRequestDto getRentalCreateRequestDto(Rental rental) {
        RentalCreateRequestDto requestDto = new RentalCreateRequestDto();
        requestDto.setRentalDate(rental.getRentalDate());
        requestDto.setReturnDate(rental.getReturnDate());
        requestDto.setCarId(rental.getCar().getId());
        return requestDto;
    }

    public static RentalResponseDto getRentalResponseDto(Rental rental) {
        RentalResponseDto responseDto = new RentalResponseDto();
        responseDto.setUserId(rental.getId());
        responseDto.setRentalDate(rental.getRentalDate());
        responseDto.setReturnDate(rental.getReturnDate());
        responseDto.setCarId(rental.getCar().getId());
        responseDto.setActualReturnDate(rental.getActualReturnDate());
        responseDto.setUserId(rental.getUser().getId());
        return responseDto;
    }

    public static String createNotificationForCreatedRental(User user, Car car, Rental rental) {
        return "User with email ["
                + user.getEmail()
                + "] start new rental, car ["
                + car.getBrand()
                + " "
                + car.getModel()
                + "], dates: "
                + rental.getRentalDate()
                + " -> "
                + rental.getReturnDate()
                + "]";
    }

    public static RentalSetActualReturnRequestDto getRentalSetActualReturnRequestDto(Rental rental) {
        return new RentalSetActualReturnRequestDto(rental.getId());
    }

    public static Pageable getDefaultRentalPageable() {
        return PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "rentalDate"));
    }
}
