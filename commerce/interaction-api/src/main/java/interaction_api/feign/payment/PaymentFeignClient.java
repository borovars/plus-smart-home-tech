package interaction_api.feign.payment;

import java.util.UUID;

import interaction_api.feign.cb.PaymentClientFallback;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.payment.model.PaymentDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment", path = "/api/v1/payment", fallback = PaymentClientFallback.class)
public interface PaymentFeignClient {

    @PostMapping
    PaymentDto processPayment(@RequestBody @Valid @NotNull OrderDto orderDto);

    @PostMapping("/totalCost")
    Double getTotalCost(@RequestBody @Valid @NotNull OrderDto orderDto);

    @PostMapping("/refund")
    void paymentSuccess(@RequestBody @NotNull UUID paymentId);

    @PostMapping("/productCost")
    Double getProductsCost(@RequestBody @Valid @NotNull OrderDto orderDto);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody @NotNull UUID paymentId);
}
