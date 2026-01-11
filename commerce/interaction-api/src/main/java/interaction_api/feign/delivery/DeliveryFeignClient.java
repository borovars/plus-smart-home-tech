package interaction_api.feign.delivery;

import java.util.UUID;

import interaction_api.feign.cb.DeliveryClientFallback;
import interaction_api.feign.delivery.model.DeliveryDto;
import interaction_api.feign.order.model.OrderDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery",path = "/api/v1/delivery", fallback = DeliveryClientFallback.class)
public interface DeliveryFeignClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody @Valid @NotNull DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void deliverySuccessful(@RequestBody @NotNull UUID deliveryId);

    @PostMapping("/picked")
    void deliveryPicked(@NotNull UUID deliveryId);

    @PostMapping("/failed")
    void deliveryFailed(@NotNull UUID deliveryId);

    @PostMapping("/cost")
    Double deliveryCost(@Valid @NotNull OrderDto orderDto);
}