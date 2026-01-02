package payment;

import interaction_api.common.exception.NoOrderFoundException;
import interaction_api.common.exception.NotEnoughInfoInOrderToCalculateException;
import interaction_api.feign.order.OrderFeignClient;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.payment.model.PaymentDto;
import interaction_api.feign.payment.model.PaymentState;
import interaction_api.feign.store.StoreFeignClient;
import interaction_api.feign.store.model.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    StoreFeignClient shoppingStoreClient;
    OrderFeignClient orderClient;

    //@Value("${payment.vatRate:0.1}")
    private double vatRate = 0.1;
    /* Здесь проблема коэффициентом. Если оставить его значение в конфиге, то оно не
    читается и приложение падает с ошибкой:
    Parameter 4 of constructor in payment.PaymentService required a bean of type 'double' that could not be found.
    Странное поведение, исправить не удалось*/
    /* Еще почему-то спринг жалуется, что нельзя использовать аннотации @RequestMapping и @FeignClient одновременно,
    поэтому я в некоторых клиентах убрал @RequestMapping и перенес путь, хотя в прошлом спринте все было в порядке
     */

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

        Payment payment = checkPayment(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);

        log.info("Изменение статуса оплаты на успешное, id оплаты: {}", paymentId);
        orderClient.payment(payment.getOrderId());
    }

    public Double getProductsCost(OrderDto order) {
        log.info("Запрос суммы заказа {}", order.getOrderId());

        checkOrder(order);
        return order.getProducts().entrySet().stream()
                .mapToDouble(entry -> shoppingStoreClient
                        .getById(entry.getKey()).getPrice() * entry.getValue()).sum();
    }

    public void emulatePaymentFailed(UUID paymentId) {
        Payment payment = checkPayment(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        log.info("Оплата не прошла, изменение состояния на неудачное id оплаты: {}", paymentId);
        orderClient.paymentFailed(payment.getOrderId());
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

