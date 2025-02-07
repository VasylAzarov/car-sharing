package dev.vasyl.car.sharing.service;

import dev.vasyl.car.sharing.dto.car.CarCreateUpdateRequestDto;
import dev.vasyl.car.sharing.dto.car.CarResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {

    Page<CarResponseDto> getAll(Pageable pageable);

    CarResponseDto getById(Long id);

    CarResponseDto save(CarCreateUpdateRequestDto carCreateUpdateRequestDto);

    CarResponseDto update(Long id, CarCreateUpdateRequestDto carDto);

    void deleteById(Long id);

}
