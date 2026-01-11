package interaction_api.feign.cb;

import interaction_api.common.exception.ServiceFallBackException;
import interaction_api.feign.cart.model.ShoppingCartDto;
import interaction_api.feign.warehouse.WarehouseFeignClient;
import interaction_api.feign.warehouse.model.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static interaction_api.common.FallbackUtil.FALLBACK_MESSAGE;

@Component
public class WarehouseClientFallback implements WarehouseFeignClient {

    @Override
    public void put(NewProductInWarehouseRequest request) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public void add(AddProductToWarehouseRequest request) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public BookedProductsDto check(ShoppingCartDto shoppingCartDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public AddressDto get() {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public void acceptReturn(Map<UUID, Integer> products) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }
}