package dev.vasyl.car.sharing.service.impl;

import dev.vasyl.car.sharing.dto.car.CarCreateUpdateRequestDto;
import dev.vasyl.car.sharing.dto.car.CarResponseDto;
import dev.vasyl.car.sharing.exception.EntityNotFoundException;
import dev.vasyl.car.sharing.mapper.CarMapper;
import dev.vasyl.car.sharing.model.Car;
import dev.vasyl.car.sharing.repository.CarRepository;
import dev.vasyl.car.sharing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Page<CarResponseDto> getAll(Pageable pageable) {
        return carMapper.toDtoPage(carRepository.findAll(pageable));
    }

    @Override
    public CarResponseDto getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Error when finding car: not found car with id ["
                                + id + "]"));
        return carMapper.toDto(car);
    }

    @Override
    public CarResponseDto save(CarCreateUpdateRequestDto carCreateUpdateRequestDto) {
        Car car = carRepository.save(carMapper.toModel(carCreateUpdateRequestDto));
        return carMapper.toDto(car);
    }

    @Override
    public CarResponseDto update(Long id, CarCreateUpdateRequestDto carCreateUpdateRequestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Error when update car: not found car with id ["
                                + id + "]"));
        carMapper.updateModelFromDto(carCreateUpdateRequestDto, car);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}
