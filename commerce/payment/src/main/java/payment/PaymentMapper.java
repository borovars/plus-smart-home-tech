package payment;


import java.util.UUID;

import interaction_api.feign.payment.model.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "totalPayment", source = "totalPrice")
    @Mapping(target = "productsTotal", source = "productPrice")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "feeTotal", source = "feeTotal")
    @Mapping(target = "deliveryTotal", source = "deliveryPrice")
    @Mapping(target = "paymentState", constant = "PENDING")
    Payment toEntity(UUID orderId, double totalPrice, double deliveryPrice, double productPrice, double feeTotal);

    @Mapping(target = "paymentId", source = "payment.id")
    PaymentDto toDto(Payment payment);
}