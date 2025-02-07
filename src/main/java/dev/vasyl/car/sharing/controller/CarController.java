package dev.vasyl.car.sharing.controller;

import dev.vasyl.car.sharing.dto.car.CarCreateUpdateRequestDto;
import dev.vasyl.car.sharing.dto.car.CarResponseDto;
import dev.vasyl.car.sharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
@Tag(name = "Car manager",
        description = "API for managing cars")
public class CarController {
    private final CarService carService;

    @PreAuthorize("permitAll()")
    @GetMapping
    @Operation(summary = "Get Car page",
            description = "Get Car page by page params or/and sort params."
                    + " Available for Costumer and Manager roles")
    public Page<CarResponseDto> getAll(@ParameterObject
                                       @PageableDefault(size = 20, sort = "model",
                                               direction = Sort.Direction.ASC)
                                               Pageable pageable) {
        return carService.getAll(pageable);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get car by id",
            description = "Get car by id. Available for Costumer and Manager roles")
    public CarResponseDto getById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new car",
            description = "Create a new car. Available for Manager role")
    public CarResponseDto save(@RequestBody @Valid
                                       CarCreateUpdateRequestDto carCreateUpdateRequestDto) {
        return carService.save(carCreateUpdateRequestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update car data",
            description = "Update car data. Available for Manager role")
    public CarResponseDto update(@PathVariable Long id,
                                 @RequestBody
                                 @Valid CarCreateUpdateRequestDto carCreateUpdateRequestDto) {
        return carService.update(id, carCreateUpdateRequestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete car by id",
            description = "Delete car by id. Available for Manager role")
    public void delete(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
