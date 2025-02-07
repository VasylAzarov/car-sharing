package dev.vasyl.car.sharing.mapper;

import dev.vasyl.car.sharing.config.MapperConfig;
import dev.vasyl.car.sharing.dto.car.CarCreateUpdateRequestDto;
import dev.vasyl.car.sharing.dto.car.CarResponseDto;
import dev.vasyl.car.sharing.model.Car;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(config = MapperConfig.class)
public interface CarMapper {

    CarResponseDto toDto(Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Car toModel(CarCreateUpdateRequestDto carCreateUpdateRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateModelFromDto(CarCreateUpdateRequestDto requestDto, @MappingTarget Car model);

    default Page<CarResponseDto> toDtoPage(Page<Car> carPage) {
        List<CarResponseDto> dtoList = carPage.getContent().stream()
                .map(this::toDto)
                .toList();
        return new PageImpl<>(dtoList, carPage.getPageable(), carPage.getTotalElements());
    }
}
