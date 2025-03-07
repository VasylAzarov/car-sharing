package dev.vasyl.car.sharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import dev.vasyl.car.sharing.service.impl.AsyncTelegramNotificationService;
import dev.vasyl.car.sharing.service.impl.RentalServiceImpl;
import dev.vasyl.car.sharing.service.impl.TelegramNotificationService;
import dev.vasyl.car.sharing.util.TestCarUtil;
import dev.vasyl.car.sharing.util.TestRentalUtil;
import dev.vasyl.car.sharing.util.TestUserUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTests {
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private AsyncTelegramNotificationService asyncTelegramNotificationService;
    @Mock
    private UserService userService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Verify rental starts successfully when car is available")
    void start_shouldReturnRental_whenCarIsAvailable() {
        RentalCreateRequestDto requestDto = TestRentalUtil.getRentalCreateRequestDto(
                TestRentalUtil.getNotCompletedRental());
        Car car = TestCarUtil.getFirstCar();
        User user = TestUserUtil.getFirstCustomer();
        Rental rental = TestRentalUtil.getNotCompletedRental();
        RentalResponseDto expectedResponseDto = TestRentalUtil.getRentalResponseDto(
                TestRentalUtil.getNotCompletedRental());
        String expectedMessage
                = TestRentalUtil.createNotificationForCreatedRental(user, car, rental);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getCurrentUser()).thenReturn(user);
        when(rentalMapper.toModelByDtoAndCarAndUser(requestDto, car, user)).thenReturn(rental);
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expectedResponseDto);

        RentalResponseDto result = rentalService.start(requestDto);

        assertNotNull(result);
        assertEquals(expectedResponseDto.getCarId(), result.getCarId());
        verify(carRepository).findById(1L);
        verify(userService).getCurrentUser();
        verify(rentalMapper).toModelByDtoAndCarAndUser(requestDto, car, user);
        verify(carRepository).save(car);
        verify(rentalRepository).save(rental);
        verify(rentalMapper).toDto(rental);
        verify(asyncTelegramNotificationService).sendNotification(expectedMessage);
    }

    @Test
    @DisplayName("Verify exception is thrown when no cars are available")
    void start_shouldThrowNoAvailableCarsException_whenNoCarsAvailable() {
        RentalCreateRequestDto requestDto = TestRentalUtil.getRentalCreateRequestDto(
                TestRentalUtil.getNotCompletedRental());
        Car car = TestCarUtil.getFirstCar();
        car.setInventory(0);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        NoAvailableCarsException exception = assertThrows(
                NoAvailableCarsException.class,
                () -> rentalService.start(requestDto)
        );

        assertEquals("Error when processing rental: car with id ["
                + car.getId() + "] No available cars", exception.getMessage());
    }

    @Test
    @DisplayName("Verify exception is thrown when car not found")
    void start_shouldThrowEntityNotFoundException_whenCarNotFound() {
        RentalCreateRequestDto requestDto = TestRentalUtil.getRentalCreateRequestDto(
                TestRentalUtil.getNotCompletedRental());
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.start(requestDto)
        );

        assertEquals("Error when start rental: car with id ["
                + requestDto.getCarId() + "] Not found", exception.getMessage());
    }

    @Test
    @DisplayName("Verify not completed rentals are returned by parameters")
    void getByParams_shouldReturnNotCompletedRentals_whenValidParams() {
        Pageable pageable = TestRentalUtil.getDefaultRentalPageable();
        List<RentalResponseDto> rentalResponseDtos =
                List.of(TestRentalUtil.getRentalResponseDto(
                        TestRentalUtil.getNotCompletedRental()));

        PageImpl<Rental> rentalsPage = new PageImpl<>(
                List.of(TestRentalUtil.getNotCompletedRental()),
                pageable,
                20
        );

        when(rentalRepository.findAllByUserIdAndActualReturnDateIsNull(pageable, 1L))
                .thenReturn(rentalsPage);
        when(rentalMapper.toDtoPage(rentalsPage))
                .thenReturn(new PageImpl<>(rentalResponseDtos));

        Page<RentalResponseDto> result = rentalService.getByParams(
                pageable,
                1L,
                true
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(rentalRepository).findAllByUserIdAndActualReturnDateIsNull(any(Pageable.class), anyLong());
        verify(rentalMapper).toDtoPage(rentalsPage);
    }

    @Test
    @DisplayName("Verify completed rentals are returned by parameters")
    void getByParams_shouldReturnCompletedRentals_whenValidParams() {
        PageImpl<Rental> rentalsPage = new PageImpl<>(
                List.of(TestRentalUtil.getCompletedRental()),
                TestRentalUtil.getDefaultRentalPageable(),
                20);

        when(rentalRepository.findAllByUserIdAndActualReturnDateIsNotNull(
                TestRentalUtil.getDefaultRentalPageable(),
                1L))
                .thenReturn(rentalsPage);
        when(rentalMapper.toDtoPage(rentalsPage))
                .thenReturn(new PageImpl<>(List.of(TestRentalUtil.getRentalResponseDto(
                        TestRentalUtil.getCompletedRental()))));

        Page<RentalResponseDto> result = rentalService.getByParams(
                TestRentalUtil.getDefaultRentalPageable(),
                1L,
                false);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(rentalRepository).findAllByUserIdAndActualReturnDateIsNotNull(
                TestRentalUtil.getDefaultRentalPageable(),
                1L);
        verify(rentalMapper).toDtoPage(rentalsPage);
    }

    @Test
    @DisplayName("Verify rental is returned by ID")
    void getById_shouldReturnRental_whenValidId() {
        Rental rental = TestRentalUtil.getNotCompletedRental();
        RentalResponseDto expectedResponseDto = TestRentalUtil.getRentalResponseDto(rental);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expectedResponseDto);

        RentalResponseDto result = rentalService.getById(1L);

        assertNotNull(result);
        assertEquals(expectedResponseDto.getCarId(), result.getCarId());
        verify(rentalRepository).findById(1L);
    }

    @Test
    @DisplayName("Verify exception is thrown when rental not found by ID")
    void getById_shouldThrowEntityNotFoundException_whenRentalNotFound() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.getById(1L)
        );

        assertEquals("Error when finding rental by id [1]: Not found", exception.getMessage());
    }

    @Test
    @DisplayName("Verify rental is completed successfully")
    void complete_shouldReturnRental_whenValidRental() {
        Rental rental = TestRentalUtil.getNotCompletedRental();
        Car car = TestCarUtil.getFirstCar();
        RentalResponseDto expectedResponseDto = TestRentalUtil.getRentalResponseDto(rental);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(rentalMapper.toDto(rental)).thenReturn(expectedResponseDto);

        RentalResponseDto result = rentalService.complete(
                new RentalSetActualReturnRequestDto(1L));

        assertNotNull(result);
        verify(rentalRepository).findById(1L);
        verify(carRepository).findById(1L);
        verify(rentalRepository).save(rental);
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("Verify exception is thrown when rental not found for completion")
    void complete_shouldThrowEntityNotFoundException_whenRentalNotFound() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.complete(new RentalSetActualReturnRequestDto(1L))
        );

        assertEquals("Error when finding rental by id [1]: Not found",
                exception.getMessage());
    }

    @Test
    @DisplayName("Verify exception is thrown when car not found for completion")
    void complete_shouldThrowEntityNotFoundException_whenCarNotFound() {
        Rental rental = TestRentalUtil.getNotCompletedRental();
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.complete(new RentalSetActualReturnRequestDto(1L))
        );

        assertEquals("Error when start rental: car with id ["
                + rental.getCar().getId() + "] Not found", exception.getMessage());
    }
}
