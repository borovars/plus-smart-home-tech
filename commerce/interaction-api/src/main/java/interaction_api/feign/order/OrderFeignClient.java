package interaction_api.feign.order;

import java.util.List;
import java.util.UUID;

import interaction_api.feign.cb.OrderClientFallback;
import interaction_api.feign.order.model.CreateNewOrderRequest;
import interaction_api.feign.order.model.OrderDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service", fallback = OrderClientFallback.class)
@RequestMapping("/api/v1/order")
public interface OrderFeignClient {

    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam @NotBlank String userName);

    @PutMapping
    OrderDto createNewOrder(@RequestBody @Valid @NotNull CreateNewOrderRequest request, @RequestParam @NotBlank String userName);

    @PostMapping("/payment")
    OrderDto payment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody UUID orderId);
}
