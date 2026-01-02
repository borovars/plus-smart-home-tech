package payment;

import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.payment.PaymentFeignClient;
import interaction_api.feign.payment.model.PaymentDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentFeignClient {

    private final PaymentService paymentService;

    @Override
    public PaymentDto processPayment(OrderDto orderDto) {
        return paymentService.processPayment(orderDto);
    }

    @Override
    public Double getTotalCost(OrderDto orderDto) {
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        paymentService.emulatePaymentSuccess(paymentId);
    }

    @Override
    public Double getProductsCost(OrderDto orderDto) {
        return paymentService.getProductsCost(orderDto);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        paymentService.emulatePaymentFailed(paymentId);
    }
}