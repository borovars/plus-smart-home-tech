package interaction_api.feign.cb;

import interaction_api.common.exception.ServiceFallBackException;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.payment.PaymentFeignClient;
import interaction_api.feign.payment.model.PaymentDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static interaction_api.common.FallbackUtil.FALLBACK_MESSAGE;

@Component
public class PaymentClientFallback implements PaymentFeignClient {


    @Override
    public PaymentDto processPayment(OrderDto orderDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }


    @Override
    public Double getTotalCost(OrderDto orderDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }


    @Override
    public void paymentSuccess(UUID paymentId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }


    @Override
    public Double getProductsCost(OrderDto orderDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }


    @Override
    public void paymentFailed(UUID paymentId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

}