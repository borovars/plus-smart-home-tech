package order;


import interaction_api.common.exception.NoOrderFoundException;
import interaction_api.feign.delivery.DeliveryFeignClient;
import interaction_api.feign.delivery.model.DeliveryDto;
import interaction_api.feign.delivery.model.DeliveryState;
import interaction_api.feign.order.model.CreateNewOrderRequest;
import interaction_api.feign.order.model.OrderDto;
import interaction_api.feign.order.model.OrderState;
import interaction_api.feign.order.model.ProductReturnRequest;
import interaction_api.feign.payment.PaymentFeignClient;
import interaction_api.feign.payment.model.PaymentDto;
import interaction_api.feign.warehouse.WarehouseFeignClient;
import interaction_api.feign.warehouse.model.AddressDto;
import interaction_api.feign.warehouse.model.AssemblyProductsForOrderRequest;
import interaction_api.feign.warehouse.model.BookedProductsDto;
import interaction_api.feign.warehouse.model.exception.NoSpecifiedProductInWarehouseException;

import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import order.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OrderMapper orderMapper;
    PaymentFeignClient paymentClient;
    DeliveryFeignClient deliveryClient;
    WarehouseFeignClient warehouseClient;

    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String userName) {
        log.info("Запрос заказов клиента {}", userName);

        checkUser(userName);

        return orderRepository.findAllByUserName(userName).stream()
                .map(orderMapper::toDto).toList();
    }

    public OrderDto createNewOrder(CreateNewOrderRequest request, String userName) throws NoSpecifiedProductInWarehouseException {
        log.info("Запрос на создание нового заказа для пользователя {}", userName);

        checkUser(userName);
        log.debug("Вызов warehouseClient.check с данными: {}", request.getShoppingCart());
        BookedProductsDto bookedProductsDto = warehouseClient.check(request.getShoppingCart());
        log.debug("Ответ от warehouseClient: {}", bookedProductsDto);
        Order order = orderMapper.toEntity(request, userName, bookedProductsDto);
        orderRepository.save(order);

        log.debug("Заказ успешно создан: {}", order);
        return orderMapper.toDto(order);
    }

    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Запрос на возврат товара {}", request.getOrderId());

        Order order = checkAndReturnOrder(request.getOrderId());
        log.debug("Вызов warehouseClient.acceptReturn с данными: {}", order.getProducts());
        warehouseClient.acceptReturn(order.getProducts());
        log.debug("Заказ {} успешно возвращен", order);
        order.setState(OrderState.PRODUCT_RETURNED);

        log.info("Заказ {} успешно возвращен", order.getId());
        return orderMapper.toDto(order);
    }

    public OrderDto payment(UUID orderId) {
        log.info("Запрос оплаты заказа {} ", orderId);

        Order order = checkAndReturnOrder(orderId);
        log.debug("Вызов paymentClient.processPayment с данными: {}", orderMapper.toDto(order));
        PaymentDto paymentDto = paymentClient.processPayment(orderMapper.toDto(order));
        log.debug("Ответ от paymentClient: {}", paymentDto);
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
        log.debug("Вызов warehouseClient.get");
        AddressDto addressFrom = warehouseClient.get();
        log.debug("Ответ от warehouseClient: {}", addressFrom);
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .toAddress(orderMapper.toAddressDto(order.getDeliveryAddress()))
                .fromAddress(addressFrom)
                .build();
        log.debug("Вызов deliveryClient.planDelivery c данными {}", deliveryDto);
        deliveryDto = deliveryClient.planDelivery(deliveryDto);
        log.debug("Ответ от deliveryClient: {}", deliveryDto);
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
        log.debug("Вызов paymentClient.getTotalCost c данными {}", orderMapper.toDto(order));
        Double totalCost = paymentClient.getTotalCost(orderMapper.toDto(order));
        log.debug("Ответ от paymentClient: {}", totalCost);
        order.setTotalPrice(totalCost);
        log.info("Общая цена заказа {} составляет {}", orderId, totalCost);

        return orderMapper.toDto(order);
    }

    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Запрос расчета стоимости доставки заказа {}", orderId);

        Order order = checkAndReturnOrder(orderId);
        log.debug("Вызов deliveryClient.deliveryCost c данными {}", orderMapper.toDto(order));
        Double deliveryCost = deliveryClient.deliveryCost(orderMapper.toDto(order));
        log.debug("Ответ от deliveryClient: {}", deliveryCost);
        order.setDeliveryPrice(deliveryCost);
        log.info("Стоимость доставки для заказа {} составляет {}", orderId, deliveryCost);

        return orderMapper.toDto(order);
    }

    public OrderDto assembly(UUID orderId) {
        log.info("Запрос сборки заказа {}", orderId);

        Order order = checkAndReturnOrder(orderId);
        AssemblyProductsForOrderRequest assemblyRequest = orderMapper.toAssemblyRequest(order);
        log.debug("Вызов warehouseClient.assemblyProducts c данными {}", assemblyRequest);
        BookedProductsDto bookedProductsDto = warehouseClient.assemblyProducts(assemblyRequest);
        log.debug("Ответ от warehouseClient: {}", bookedProductsDto);
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
