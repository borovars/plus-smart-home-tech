package warehouse;

import interaction_api.feign.cart.model.ShoppingCartDto;
import interaction_api.feign.warehouse.WarehouseFeignClient;
import interaction_api.feign.warehouse.model.*;
import interaction_api.feign.warehouse.model.exception.NoSpecifiedProductInWarehouseException;
import interaction_api.feign.warehouse.model.exception.SpecifiedProductAlreadyInWarehouseException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseFeignClient {

    private final WarehouseService service;

    @Override
    public void put(NewProductInWarehouseRequest newProductInWarehouseRequest) throws SpecifiedProductAlreadyInWarehouseException {
        service.putNewProduct(newProductInWarehouseRequest);
    }

    @Override
    public BookedProductsDto check(ShoppingCartDto shoppingCartDto) throws NoSpecifiedProductInWarehouseException {
       return service.check(shoppingCartDto);
    }

    @Override
    public void add(AddProductToWarehouseRequest addProductToWarehouseRequest)
            throws NoSpecifiedProductInWarehouseException {
        service.addProduct(addProductToWarehouseRequest);
    }

    @Override
    public AddressDto get(){
        return service.getAddress();
    }

    @Override
    public void acceptReturn(Map<UUID, Integer> products) {
        service.acceptReturn(products);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        service.shippedToDelivery(request);
    }

    @Override
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        return service.assemblyProductsForOrder(request);
    }
}
