package dev.vasyl.car.sharing.mapper;

import dev.vasyl.car.sharing.config.MapperConfig;
import dev.vasyl.car.sharing.dto.rental.RentalCreateRequestDto;
import dev.vasyl.car.sharing.dto.rental.RentalResponseDto;
import dev.vasyl.car.sharing.model.Car;
import dev.vasyl.car.sharing.model.Rental;
import dev.vasyl.car.sharing.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "car", source = "car")
    @Mapping(target = "user", source = "user")
    Rental toModelByDtoAndCarAndUser(RentalCreateRequestDto requestDto, Car car, User user);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toDto(Rental rental);

    default Page<RentalResponseDto> toDtoPage(Page<Rental> rentalPage) {
        List<RentalResponseDto> dtoList = rentalPage.getContent().stream()
                .map(this::toDto)
                .toList();
        return new PageImpl<>(dtoList, rentalPage.getPageable(), rentalPage.getTotalElements());
    }
}
