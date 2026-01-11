package interaction_api.feign.order.model;

import interaction_api.feign.cart.model.ShoppingCartDto;
import interaction_api.feign.warehouse.model.AddressDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateNewOrderRequest {

    @NotNull
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto deliveryAddress;
}