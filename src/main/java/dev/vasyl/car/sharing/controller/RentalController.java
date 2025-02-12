package dev.vasyl.car.sharing.controller;

import dev.vasyl.car.sharing.dto.rental.RentalCreateRequestDto;
import dev.vasyl.car.sharing.dto.rental.RentalResponseDto;
import dev.vasyl.car.sharing.dto.rental.RentalSetActualReturnRequestDto;
import dev.vasyl.car.sharing.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
@Tag(name = "Rental manager",
        description = "API for managing rentals")
public class RentalController {
    private final RentalService rentalService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start rental",
            description = "Start rental with specific details."
                    + " Available for Costumer and Manager roles")
    public RentalResponseDto startRental(@RequestBody @Valid RentalCreateRequestDto requestDto) {
        return rentalService.start(requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    @Operation(summary = "Get rentals page",
            description = "Get rentals page by specific params."
                    + " Available for Manager roles")
    public Page<RentalResponseDto> getAllByParams(@ParameterObject
                                                  @PageableDefault(size = 20, sort = "rentalDate",
                                                          direction = Sort.Direction.ASC)
                                                          Pageable pageable,
                                                  @RequestParam("userId") @NotNull
                                                  @Positive Long userId,
                                                  @RequestParam("isActive") @NotNull
                                                              boolean isActive) {
        return rentalService.getByParams(pageable, userId, isActive);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get rental by id",
            description = "Get rental by id."
                    + " Available for Costumer and Manager roles")
    public RentalResponseDto getById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/return")
    @Operation(summary = "Complete rental",
            description = "Complete rental."
                    + " Available for Costumer and Manager roles")
    public RentalResponseDto completeRental(
            @RequestBody @Valid RentalSetActualReturnRequestDto requestDto) {
        return rentalService.complete(requestDto);
    }
}
