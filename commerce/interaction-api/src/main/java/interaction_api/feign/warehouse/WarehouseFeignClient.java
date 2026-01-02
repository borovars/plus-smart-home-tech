package interaction_api.feign.warehouse;

import interaction_api.feign.cart.model.ShoppingCartDto;
import interaction_api.feign.cb.WarehouseClientFallback;
import interaction_api.feign.warehouse.model.*;
import interaction_api.feign.warehouse.model.exception.NoSpecifiedProductInWarehouseException;
import interaction_api.feign.warehouse.model.exception.SpecifiedProductAlreadyInWarehouseException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseFeignClient {

    @PutMapping
    void put(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest) throws SpecifiedProductAlreadyInWarehouseException;

    @PostMapping("/check")
    BookedProductsDto check(@RequestBody @Valid ShoppingCartDto shoppingCartDto) throws NoSpecifiedProductInWarehouseException;

    @PostMapping("/add")
    void add(@RequestBody @Valid AddProductToWarehouseRequest addProductToWarehouseRequest) throws NoSpecifiedProductInWarehouseException;

    @GetMapping("/address")
    AddressDto get();

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Integer> products);

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody @Valid @NotNull ShippedToDeliveryRequest request);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProducts(@RequestBody @Valid @NotNull AssemblyProductsForOrderRequest request);
}
