package interaction_api.feign.cb;

import java.util.UUID;

import interaction_api.common.exception.ServiceFallBackException;
import interaction_api.feign.delivery.DeliveryFeignClient;
import interaction_api.feign.delivery.model.DeliveryDto;
import interaction_api.feign.order.model.OrderDto;
import org.springframework.stereotype.Component;

import static interaction_api.common.FallbackUtil.FALLBACK_MESSAGE;


@Component
public class DeliveryClientFallback implements DeliveryFeignClient {

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public Double deliveryCost(OrderDto orderDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }
}