package delivery;

import delivery.model.Address;
import delivery.model.Delivery;
import interaction_api.common.exception.NoDeliveryFoundException;
import interaction_api.feign.delivery.model.DeliveryDto;
import interaction_api.feign.delivery.model.DeliveryState;
import interaction_api.feign.order.OrderFeignClient;
import interaction_api.feign.order.model.OrderDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

    private static final Double BASE_PRICE = 5.0;
    private static final Double ANOTHER_ADDRESS_RATIO = 2.0;
    private static final Double FRAGILE_RATIO = 0.2;
    private static final Double WEIGHT_RATIO = 0.3;
    private static final Double VOLUME_RATIO = 0.2;
    private static final String ADDRESS_2 = "ADDRESS_2";
    private static final Double ANOTHER_STREET_RATIO = 0.2;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderFeignClient orderClient;

    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("запрос планирования доставки");

        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);

        log.info("Доставка успешно создана {} ", delivery);
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    public void deliverySuccessful(UUID deliveryId) {
        log.info("Запрос изменения состояния доставки {} на выполненное", deliveryId);

        Delivery delivery = checkAndGetDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);

        log.info("Изменение статуса доставки {} успешно выполнено", deliveryId);
        log.debug("Вызов orderClient.delivery с данными: {}", delivery.getOrderId());
        OrderDto response = orderClient.delivery(delivery.getOrderId());
        log.debug("Ответ от orderClient: {}", response);
    }

    public void deliveryPicked(UUID deliveryId) {
        log.info("Запрос изменения состояния доставки {} на IN_PROGRESS", deliveryId);

        Delivery delivery = checkAndGetDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        log.info("Состояние доставки {} успешно изменено на IN_PROGRESS", deliveryId);
        log.debug("Вызов orderClient.assembly с данными: {}", delivery.getOrderId());
        OrderDto response = orderClient.assembly(delivery.getOrderId());
        log.debug("Ответ от orderClient: {}", response);
    }

    public void deliveryFailed(UUID deliveryId) {
        log.info("Запрос изменения состояния доставки {} на FAILED", deliveryId);

        Delivery delivery = checkAndGetDelivery(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);

        log.info("Состояние доставки {} успешно изменено на FAILED", deliveryId);
        log.debug("Вызов orderClient.deliveryFailed с данными: {}", delivery.getOrderId());
        OrderDto response = orderClient.deliveryFailed(delivery.getOrderId());
        log.debug("Ответ от orderClient: {}", response);
    }

    public Double deliveryCost(OrderDto orderDto) {
        log.info("Запрос подсчета суммы доставки заказа {}", orderDto.getOrderId());

        double deliveryCost = BASE_PRICE;

        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с id %s для заказа с id %s не найдена"
                        .formatted(orderDto.getDeliveryId(), orderDto.getOrderId())));

        delivery.setFragile(orderDto.getFragile());
        delivery.setTotalWeight(orderDto.getDeliveryWeight());
        delivery.setTotalVolume(orderDto.getDeliveryVolume());

        Address warehouseAddress = delivery.getFromAddress();
        if (warehouseAddress.toString().contains(ADDRESS_2)) {
            deliveryCost *= ANOTHER_ADDRESS_RATIO;
        }
        if (delivery.isFragile()) {
            deliveryCost += deliveryCost * FRAGILE_RATIO;
        }

        deliveryCost += delivery.getTotalWeight() * WEIGHT_RATIO;
        deliveryCost += delivery.getTotalVolume() * VOLUME_RATIO;

        if (!delivery.getToAddress().getStreet().equals(warehouseAddress.getStreet())) {
            deliveryCost += deliveryCost * ANOTHER_STREET_RATIO;
        }

        log.info("Стоимость доставки успешно посчитана: {}", deliveryCost);
        return deliveryCost;
    }

    private Delivery checkAndGetDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с id %s не найдена".formatted(deliveryId)));
    }
}