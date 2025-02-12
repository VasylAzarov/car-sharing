package dev.vasyl.car.sharing.util;

import dev.vasyl.car.sharing.dto.car.CarCreateUpdateRequestDto;
import dev.vasyl.car.sharing.dto.car.CarResponseDto;
import dev.vasyl.car.sharing.model.Car;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestCarUtil {
    private static Car createCar(Long id,
                                 String model,
                                 String brand,
                                 Car.CarType type,
                                 int inventory,
                                 BigDecimal dailyFee) {
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setBrand(brand);
        car.setType(type);
        car.setInventory(inventory);
        car.setDailyFee(dailyFee);
        return car;
    }

    public static List<Car> getListOfThreeCars() {
        return List.of(
                createCar(3L,
                        "Golf",
                        "Volkswagen",
                        Car.CarType.HATCHBACK,
                        10,
                        new BigDecimal("59.99")),
                createCar(2L,
                        "X5",
                        "BMW",
                        Car.CarType.SUV,
                        3,
                        new BigDecimal("129.99")),
                createCar(1L,
                        "Model S",
                        "Tesla",
                        Car.CarType.SEDAN,
                        5,
                        new BigDecimal("99.99"))
        );
    }

    public static Car getFirstCar() {
        return getListOfThreeCars().get(0);
    }

    public static Car getLastCar() {
        return getListOfThreeCars().get(2);
    }

    public static CarResponseDto getCarResponseDto(Car car) {
        CarResponseDto dto = new CarResponseDto();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setInventory(car.getInventory());
        dto.setModel(car.getModel());
        dto.setType(car.getType());
        dto.setDailyFee(car.getDailyFee());
        return dto;
    }

    public static CarCreateUpdateRequestDto getCarCreateUpdateRequestDto(Car car) {
        CarCreateUpdateRequestDto dto = new CarCreateUpdateRequestDto();
        dto.setBrand(car.getBrand());
        dto.setInventory(car.getInventory());
        dto.setModel(car.getModel());
        dto.setType(car.getType());
        dto.setDailyFee(car.getDailyFee());
        return dto;
    }

    public static Pageable getDefaultCarPageable() {
        return PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "model"));
    }
}
