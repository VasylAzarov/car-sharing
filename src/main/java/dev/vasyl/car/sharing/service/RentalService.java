package dev.vasyl.car.sharing.service;

import dev.vasyl.car.sharing.dto.rental.RentalCreateRequestDto;
import dev.vasyl.car.sharing.dto.rental.RentalResponseDto;
import dev.vasyl.car.sharing.dto.rental.RentalSetActualReturnRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto start(RentalCreateRequestDto requestDto);

    Page<RentalResponseDto> getByParams(Pageable pageable, Long userId, boolean isActive);

    RentalResponseDto getById(Long id);

    RentalResponseDto complete(RentalSetActualReturnRequestDto requestDto);
}
