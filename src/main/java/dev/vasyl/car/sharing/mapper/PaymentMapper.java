package dev.vasyl.car.sharing.mapper;

import dev.vasyl.car.sharing.config.MapperConfig;
import dev.vasyl.car.sharing.dto.payment.PaymentResponseDto;
import dev.vasyl.car.sharing.model.Payment;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    @Mapping(source = "rental.id", target = "rentalId")
    PaymentResponseDto toDto(Payment payment);

    default Page<PaymentResponseDto> toDtoPage(Page<Payment> payments) {
        List<PaymentResponseDto> dtoList = payments.getContent().stream()
                .map(this::toDto)
                .toList();
        return new PageImpl<>(dtoList, payments.getPageable(), payments.getTotalElements());
    }
}
