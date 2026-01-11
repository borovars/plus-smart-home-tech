package interaction_api.feign.order;

import java.util.List;
import java.util.UUID;

import interaction_api.feign.cb.OrderClientFallback;
import interaction_api.feign.order.model.CreateNewOrderRequest;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.order.model.ProductReturnRequest;
import interaction_api.feign.warehouse.model.exception.NoSpecifiedProductInWarehouseException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service",path = "/api/v1/order", fallback = OrderClientFallback.class)
public interface OrderFeignClient {

    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam @NotBlank String userName);

    @PutMapping
    OrderDto createNewOrder(@RequestBody @Valid @NotNull CreateNewOrderRequest request, @RequestParam @NotBlank String userName) throws NoSpecifiedProductInWarehouseException;

    @PostMapping("/return")
    OrderDto productReturn(@RequestBody @Valid @NotNull ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto payment(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/complete")
    OrderDto complete(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody @NotNull UUID orderId);
}
