package delivery;

import delivery.model.Delivery;
import interaction_api.feign.delivery.model.DeliveryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "totalWeight", ignore = true)
    @Mapping(target = "totalVolume", ignore = true)
    @Mapping(target = "fragile", ignore = true)
    @Mapping(target = "id", source = "dto.deliveryId")
    @Mapping(target = "deliveryState", source = "dto.deliveryState")
    Delivery toEntity(DeliveryDto dto);

    @Mapping(target = "deliveryId", source = "delivery.id")
    DeliveryDto toDto(Delivery delivery);
}
