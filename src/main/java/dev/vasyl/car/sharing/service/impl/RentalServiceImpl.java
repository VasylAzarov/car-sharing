package dev.vasyl.car.sharing.service.impl;

import dev.vasyl.car.sharing.dto.rental.RentalCreateRequestDto;
import dev.vasyl.car.sharing.dto.rental.RentalResponseDto;
import dev.vasyl.car.sharing.dto.rental.RentalSetActualReturnRequestDto;
import dev.vasyl.car.sharing.exception.EntityNotFoundException;
import dev.vasyl.car.sharing.exception.NoAvailableCarsException;
import dev.vasyl.car.sharing.mapper.RentalMapper;
import dev.vasyl.car.sharing.model.Car;
import dev.vasyl.car.sharing.model.Rental;
import dev.vasyl.car.sharing.model.User;
import dev.vasyl.car.sharing.repository.CarRepository;
import dev.vasyl.car.sharing.repository.RentalRepository;
import dev.vasyl.car.sharing.service.RentalService;
import dev.vasyl.car.sharing.service.UserService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final AsyncTelegramNotificationService asyncTelegramNotificationService;
    private final UserService userService;

    @Override
    public RentalResponseDto start(RentalCreateRequestDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Error when start rental: car with id ["
                        + requestDto.getCarId() + "] Not found")
        );

        if (car.getInventory() < 1) {
            throw new NoAvailableCarsException("Error when processing rental: car with id ["
                    + car.getId() + "] No available cars");
        }

        car.setInventory(car.getInventory() - 1);
        User user = userService.getCurrentUser();
        Rental rental = rentalMapper.toModelByDtoAndCarAndUser(requestDto, car, user);

        carRepository.save(car);
        rentalRepository.save(rental);

        asyncTelegramNotificationService.sendNotification(
                createNotificationForCreatedRental(user, car, rental));

        return rentalMapper.toDto(rental);
    }

    @Override
    public Page<RentalResponseDto> getByParams(Pageable pageable, Long userId, boolean isActive) {
        Page<Rental> rentalsPage;
        if (isActive) {
            rentalsPage = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNull(pageable, userId);
        } else {
            rentalsPage = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNotNull(pageable, userId);
        }
        return rentalMapper.toDtoPage(rentalsPage);
    }

    @Override
    public RentalResponseDto getById(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Error when finding rental by id ["
                        + id + "]: Not found")
        );

        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalResponseDto complete(RentalSetActualReturnRequestDto requestDto) {
        Rental rental = rentalRepository.findById(requestDto.rentalId()).orElseThrow(
                () -> new EntityNotFoundException("Error when finding rental by id ["
                        + requestDto.rentalId() + "]: Not found")
        );

        Car car = carRepository.findById(rental.getCar().getId()).orElseThrow(
                () -> new EntityNotFoundException("Error when start rental: car with id ["
                        + rental.getCar().getId() + "] Not found")
        );

        rental.setActualReturnDate(LocalDate.now());
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        rentalRepository.save(rental);
        return rentalMapper.toDto(rental);
    }

    private String createNotificationForCreatedRental(User user, Car car, Rental rental) {
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
}
