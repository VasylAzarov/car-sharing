package dev.vasyl.car.sharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.vasyl.car.sharing.dto.car.CarCreateUpdateRequestDto;
import dev.vasyl.car.sharing.dto.car.CarResponseDto;
import dev.vasyl.car.sharing.exception.EntityNotFoundException;
import dev.vasyl.car.sharing.mapper.CarMapper;
import dev.vasyl.car.sharing.model.Car;
import dev.vasyl.car.sharing.repository.CarRepository;
import dev.vasyl.car.sharing.service.impl.CarServiceImpl;
import dev.vasyl.car.sharing.util.TestCarUtil;
import java.util.Collections;
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
public class CarServiceTests {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Verify that list of cars returned successfully")
    void getAll_shouldReturnCarsDtoPage_whenCarsExist() {
        Pageable pageable = TestCarUtil.getDefaultCarPageable();
        Page<Car> carPage = new PageImpl<>(Collections.singletonList(TestCarUtil.getFirstCar()));
        Page<CarResponseDto> carDtoPage =
                new PageImpl<>(Collections.singletonList(new CarResponseDto()));

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDtoPage(carPage)).thenReturn(carDtoPage);

        Page<CarResponseDto> result = carService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(carRepository).findAll(pageable);
        verify(carMapper).toDtoPage(carPage);
    }

    @Test
    @DisplayName("Verify that car is returned successfully when exist")
    void findById_shouldReturnCar_whenCarExists() {
        Car car = TestCarUtil.getFirstCar();
        CarResponseDto carResponseDto = TestCarUtil.getCarResponseDto(car);

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carMapper.toDto(any(Car.class))).thenReturn(carResponseDto);

        CarResponseDto result = carService.getById(1L);

        assertNotNull(result);
        assertEquals(TestCarUtil.getCarResponseDto(
                TestCarUtil.getFirstCar()).getId(), result.getId());
        verify(carRepository).findById(1L);
    }

    @Test
    @DisplayName("Verify that exception is thrown when car doesn't exist")
    void findById_shouldThrowException_whenCarDoesNotExist() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.getById(1L));

        assertEquals("Error when finding car: not found car with id ["
                + 1L + "]", exception.getMessage());
        verify(carRepository).findById(1L);
    }

    @Test
    @DisplayName("Verify that correct car is saved successfully")
    void save_shouldReturnCar_whenCarSavedSuccessfully() {
        Car car = TestCarUtil.getFirstCar();
        CarResponseDto carResponseDto = TestCarUtil.getCarResponseDto(car);
        CarCreateUpdateRequestDto requestDto
                = TestCarUtil.getCarCreateUpdateRequestDto(car);

        when(carMapper.toModel(any(CarCreateUpdateRequestDto.class))).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(carMapper.toDto(any(Car.class))).thenReturn(carResponseDto);

        CarResponseDto result = carService.save(requestDto);

        assertNotNull(result);
        assertEquals(TestCarUtil.getCarResponseDto(car).getId(), result.getId());
        verify(carRepository).save(any(Car.class));
        verify(carMapper).toModel(any(CarCreateUpdateRequestDto.class));
        verify(carMapper).toDto(any(Car.class));
    }

    @Test
    @DisplayName("Verify that correct car is updated successfully")
    void update() {
        doAnswer(invocation -> {
            CarCreateUpdateRequestDto dto = invocation.getArgument(0);
            Car car = invocation.getArgument(1);
            car.setInventory(dto.getInventory());
            car.setType(dto.getType());
            car.setBrand(dto.getBrand());
            car.setModel(dto.getModel());
            car.setDailyFee(dto.getDailyFee());
            return null;
        }).when(carMapper)
                .updateModelFromDto(any(CarCreateUpdateRequestDto.class), any(Car.class));
        when(carRepository.save(any(Car.class)))
                .thenReturn(TestCarUtil.getFirstCar());
        when(carRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(TestCarUtil.getFirstCar()));
        when(carMapper.toDto(any(Car.class)))
                .thenReturn(TestCarUtil.getCarResponseDto(TestCarUtil.getFirstCar()));

        CarResponseDto result = carService.update(1L,
                TestCarUtil.getCarCreateUpdateRequestDto(TestCarUtil.getFirstCar()));

        assertNotNull(result);
        assertEquals(TestCarUtil.getCarResponseDto(
                TestCarUtil.getFirstCar()).getId(), result.getId());
        verify(carRepository).save(any(Car.class));
        verify(carRepository).findById(any(Long.class));
        verify(carMapper).updateModelFromDto(any(CarCreateUpdateRequestDto.class), any(Car.class));
        verify(carMapper).toDto(any(Car.class));
    }

    @Test
    @DisplayName("Verify that exception is thrown when updating a non-existing car")
    void update_shouldThrowException_whenCarDoesNotExist() {
        Long carId = 999L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> carService.update(carId,
                        TestCarUtil.getCarCreateUpdateRequestDto(TestCarUtil.getFirstCar())));

        assertEquals("Error when update car: not found car with id [999]",
                exception.getMessage());
        verify(carRepository).findById(carId);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Verify that car is deleted successfully when it exists")
    void deleteById_shouldDeleteCar_whenCarExists() {
        Long carId = 1L;

        when(carRepository.findById(carId)).thenReturn(Optional.of(TestCarUtil.getFirstCar()));

        carService.deleteById(carId);

        verify(carRepository).findById(carId);
        verify(carRepository).deleteById(carId);
    }

    @Test
    @DisplayName("Verify that exception is thrown when deleting a non-existing car")
    void deleteById_shouldThrowException_whenCarDoesNotExist() {
        Long carId = 999L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> carService.deleteById(carId));

        assertEquals("Error when delete car: not found car with id [999]", exception.getMessage());
        verify(carRepository).findById(carId);
        verify(carRepository, never()).deleteById(anyLong());
    }
}
