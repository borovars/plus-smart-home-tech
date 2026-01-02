package order;


import interaction_api.common.exception.NoOrderFoundException;
import interaction_api.feign.delivery.model.DeliveryDto;
import interaction_api.feign.delivery.model.DeliveryState;
import interaction_api.feign.order.model.CreateNewOrderRequest;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.order.model.OrderState;
import interaction_api.feign.order.model.ProductReturnRequest;
import interaction_api.feign.payment.model.PaymentDto;
import interaction_api.feign.warehouse.model.AddressDto;
import interaction_api.feign.warehouse.model.AssemblyProductsForOrderRequest;
import interaction_api.feign.warehouse.model.BookedProductsDto;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import order.model.Order;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;

    public List<OrderDto> getClientOrders(String userName) {
        log.info("Запрос заказов клиента {}", userName);

        checkUser(userName);

        return orderRepository.findAllByUserName(userName).stream()
                .map(orderMapper::toDto).toList();
    }

    public OrderDto createNewOrder(CreateNewOrderRequest request, String userName) {
        log.info("Запрос на создание нового заказа для пользователя {}", userName);

        checkUser(userName);
        BookedProductsDto bookedProductsDto = warehouseClient.checkQuantity(request.getShoppingCart());
        log.debug("Booked products for order: {}", bookedProductsDto);
        Order order = orderMapper.toEntity(request, userName, bookedProductsDto);
        orderRepository.save(order);

        log.debug("Заказ успешно создан: {}", order);
        return orderMapper.toDto(order);
    }

    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Запрос на возврат товара {}", request.getOrderId());

        Order order = checkAndReturnOrder(request.getOrderId());
        warehouseClient.acceptReturn(order.getProducts());
        log.info("Заказ {} успешно возвращен", order);
        order.setState(OrderState.PRODUCT_RETURNED);

        return orderMapper.toDto(order);
    }

    public OrderDto payment(UUID orderId) {
        log.info("Запрос оплаты заказа {} ", orderId);

        Order order = checkAndReturnOrder(orderId);
        PaymentDto paymentDto = paymentClient.processPayment(orderMapper.toDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);

        log.info("Оплата заказа {} прошла успешно", orderId);
        return orderMapper.toDto(order);
    }

    public OrderDto paymentFailed(UUID orderId) {
        log.info("Запрос оплаты заказа {} ", orderId);

        Order order = checkAndReturnOrder(orderId);
        if (order.getState() == OrderState.ON_PAYMENT) {
            order.setState(OrderState.PAYMENT_FAILED);
            log.info("Во время оплаты произошла ошибка");
        } else {
            log.warn("Заказ {} не в состоянии ON PAYMENT", orderId);
        }
        return orderMapper.toDto(order);
    }

    public OrderDto delivery(UUID orderId) {
        log.info("Запрос доставки заказа {}", orderId);

        Order order = checkAndReturnOrder(orderId);
        AddressDto addressFrom = warehouseClient.getWarehouseAddress();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .toAddress(orderMapper.toAddressDto(order.getDeliveryAddress()))
                .fromAddress(addressFrom)
                .build();
        deliveryDto = deliveryClient.planDelivery(deliveryDto);
        log.debug("Детали доставки: {}", deliveryDto);
        order.setDeliveryId(deliveryDto.getDeliveryId());
        order.setState(OrderState.ON_DELIVERY);

        log.info("Заказ {}  процессе доставки", orderId);
        return orderMapper.toDto(order);
    }

    public OrderDto deliveryFailed(UUID orderId) {

        Order order = checkAndReturnOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        log.info("Заказ с id: {} не был доставлен", order);

        return orderMapper.toDto(order);
    }

    public OrderDto complete(UUID orderId) {

        Order order = checkAndReturnOrder(orderId);
        order.setState(OrderState.COMPLETED);
        log.info("Доставка заказа с id: {} успешно выполнена", order);

        return orderMapper.toDto(order);
    }

    public OrderDto calculateTotalCost(UUID orderId) {

        Order order = checkAndReturnOrder(orderId);
        Double totalCost = paymentClient.getTotalCost(orderMapper.toDto(order));
        order.setTotalPrice(totalCost);
        log.info("Total cost for order {} is {}", orderId, totalCost);

        return orderMapper.toDto(order);
    }

    public OrderDto calculateDeliveryCost(UUID orderId) {

        Order order = checkAndReturnOrder(orderId);
        Double deliveryCost = deliveryClient.deliveryCost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);
        log.info("Стоимость доставки для заказа {} составляет {}", orderId, deliveryCost);

        return orderMapper.toDto(order);
    }

    public OrderDto assembly(UUID orderId) {

        Order order = checkAndReturnOrder(orderId);
        AssemblyProductsForOrderRequest assemblyRequest = orderMapper.toAssemblyRequest(order);
        BookedProductsDto bookedProductsDto = warehouseClient.assemblyProductsForOrder(assemblyRequest);
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setIsFragile(bookedProductsDto.getIsFragile());
        order.setState(OrderState.ASSEMBLED);
        log.info("Заказ: {} успешно собран", order);

        return orderMapper.toDto(order);
    }

    public OrderDto assemblyFailed(UUID orderId) {

        Order order = checkAndReturnOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        log.warn("Ошибка сборки заказа {} ", order);

        return orderMapper.toDto(order);
    }

    private void checkUser(String userName) {
        if (userName.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
    }

    private Order checkAndReturnOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с id " + orderId + " не найден"));
    }
}
