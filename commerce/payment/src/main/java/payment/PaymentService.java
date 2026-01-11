package payment;

import interaction_api.common.exception.NoOrderFoundException;
import interaction_api.common.exception.NotEnoughInfoInOrderToCalculateException;
import interaction_api.feign.order.OrderFeignClient;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.payment.model.PaymentDto;
import interaction_api.feign.payment.model.PaymentState;
import interaction_api.feign.store.StoreFeignClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StoreFeignClient shoppingStoreClient;
    private final OrderFeignClient orderClient;

    @Value("${payment.vatRate:0.1}")
    double vatRate;

    @Transactional
    public PaymentDto processPayment(OrderDto order) {
        log.info("Запрос на оплату {} заказа", order.getOrderId());

        checkOrder(order);
        Payment payment = paymentRepository
                .save(paymentMapper
                        .toEntity(order.getOrderId(),
                                order.getTotalPrice(),
                                order.getDeliveryPrice(),
                                order.getTotalPrice(),
                                order.getTotalPrice() * vatRate));

        log.info("Оплата прошла успешно id оплаты: {}", payment.getId());
        return paymentMapper.toDto(payment);
    }

    public Double getTotalCost(OrderDto order) {
        log.info("Запрос суммы заказа {}", order.getOrderId());

        checkOrder(order);

        log.info("Полная сумма для заказа id: {} успешно посчитана", order.getOrderId());
        return order.getDeliveryPrice() + order.getProductPrice() * vatRate + order.getProductPrice();
    }

    @Transactional
    public void emulatePaymentSuccess(UUID paymentId) {
        log.info("Эмуляция успешной оплаты {}", paymentId);

        Payment payment = checkPayment(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);

        log.info("Изменение статуса оплаты на успешное, id оплаты: {}", paymentId);
        log.debug("Вызов orderClient.payment c данными {}", payment.getOrderId());
        orderClient.payment(payment.getOrderId());
        log.debug("Эмуляция успешной оплаты успешно выполнена");
    }

    @Transactional(readOnly = true)
    public Double getProductsCost(OrderDto order) {
        log.info("Запрос суммы заказа {}", order.getOrderId());

        checkOrder(order);
        return order.getProducts().entrySet().stream()
                .mapToDouble(entry -> shoppingStoreClient
                        .getById(entry.getKey()).getPrice() * entry.getValue()).sum();
    }

    public void emulatePaymentFailed(UUID paymentId) {
        log.info("Эмуляция НЕ успешной оплаты {}", paymentId);

        Payment payment = checkPayment(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        log.info("Оплата не прошла, изменение состояния на неудачное id оплаты: {}", paymentId);
        log.debug("Вызов orderClient.paymentFailed c данными {}", payment.getOrderId());
        orderClient.paymentFailed(payment.getOrderId());
        log.debug("Эмуляция НЕ успешной оплаты успешно выполнена");
    }

    private void checkOrder(OrderDto order) {
        if (order.getTotalPrice() == null || order.getDeliveryPrice() == null || order.getOrderId() == null
                || order.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Недостаточно информации для оплаты заказа, id заказа %s"
                    .formatted(order.getOrderId()));
        }
    }

    private Payment checkPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Оплата с id: %s не найдена".formatted(paymentId)));
    }
}

