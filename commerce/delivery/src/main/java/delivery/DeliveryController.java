package delivery;

import interaction_api.feign.delivery.DeliveryFeignClient;
import interaction_api.feign.delivery.model.DeliveryDto;
import interaction_api.feign.order.model.OrderDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryFeignClient {

    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        deliveryService.deliverySuccessful(deliveryId);
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        deliveryService.deliveryPicked(deliveryId);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        deliveryService.deliveryFailed(deliveryId);
    }

    @Override
    public Double deliveryCost(OrderDto orderDto) {
        return deliveryService.deliveryCost(orderDto);
    }
}