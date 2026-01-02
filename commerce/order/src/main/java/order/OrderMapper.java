package order;

import interaction_api.feign.order.model.CreateNewOrderRequest;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.order.model.OrderState;
import interaction_api.feign.warehouse.model.AddressDto;
import interaction_api.feign.warehouse.model.AssemblyProductsForOrderRequest;
import interaction_api.feign.warehouse.model.BookedProductsDto;
import order.model.Address;
import order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "fragile", source = "order.isFragile")
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    Address toAddress(AddressDto addressDto);

    AddressDto toAddressDto(Address address);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "products", source = "order.products")
    AssemblyProductsForOrderRequest toAssemblyRequest(Order order);

    default Order toEntity(CreateNewOrderRequest request, String userName, BookedProductsDto bookedProducts) {
        return Order.builder()
                .state(OrderState.NEW)
                .userName(userName)
                .shoppingCartId(request.getShoppingCart().getCartId())
                .deliveryAddress(toAddress(request.getDeliveryAddress()))
                .products(request.getShoppingCart().getProducts())
                .isFragile(bookedProducts.getIsFragile())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .build();
    }
}
