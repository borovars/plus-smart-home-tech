package order;

import interaction_api.feign.order.OrderFeignClient;
import interaction_api.feign.order.model.CreateNewOrderRequest;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.order.model.ProductReturnRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController implements OrderFeignClient {

    private final OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        log.info("Get orders for user {}", userName);
        return orderService.getClientOrders(userName);
    }

    public OrderDto createNewOrder(CreateNewOrderRequest request, String userName) {
        log.info("Create new order for user {}", userName);
        return orderService.createNewOrder(request, userName);
    }

    public OrderDto productReturn(@RequestBody @Valid @NotNull ProductReturnRequest request) {
        log.info("Product return request {}", request.toString());
        return orderService.productReturn(request);
    }

    @PostMapping("/payment")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto payment(@RequestBody @NotNull UUID orderId) {
        log.info("Payment, orderId: {}", orderId);
        return orderService.payment(orderId);
    }

    @PostMapping("/payment/failed")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto paymentFailed(@RequestBody @NotNull UUID orderId) {
        log.info("Payment failed, orderId {}", orderId);
        return orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto delivery(@RequestBody @NotNull UUID orderId) {
        log.info("Delivery, orderId {}", orderId);
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto deliveryFailed(@RequestBody @NotNull UUID orderId) {
        log.info("Delivery failed, orderId: {}", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/complete")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto complete(@RequestBody @NotNull UUID orderId) {
        log.info("Complete order, orderId: {}", orderId);
        return orderService.complete(orderId);
    }

    @PostMapping("/calculate/total")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto calculateTotalCost(@RequestBody @NotNull UUID orderId) {
        log.info("Calculate total cost, orderId: {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @PostMapping("/calculate/delivery")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto calculateDeliveryCost(@RequestBody @NotNull UUID orderId) {
        log.info("Calculate delivery cost, orderId: {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @PostMapping("/assembly")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto assembly(@RequestBody @NotNull UUID orderId) {
        log.info("Assembly, orderId: {}", orderId);
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto assemblyFailed(@RequestBody @NotNull UUID orderId) {
        log.info("Assembly failed, orderId: {}", orderId);
        return orderService.assemblyFailed(orderId);
    }


}
