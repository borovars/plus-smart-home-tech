package interaction_api.feign.cb;

import interaction_api.common.exception.ServiceFallBackException;
import interaction_api.feign.order.OrderFeignClient;
import interaction_api.feign.order.model.CreateNewOrderRequest;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.order.model.ProductReturnRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static interaction_api.common.FallbackUtil.FALLBACK_MESSAGE;

@Component
public class OrderClientFallback implements OrderFeignClient {

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request, String userName) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto payment(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto delivery(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto complete(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto assembly(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());    }
}