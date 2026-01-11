package warehouse;

import interaction_api.common.exception.LowQuantityException;
import interaction_api.common.exception.NoOrderFoundException;
import interaction_api.feign.cart.model.ShoppingCartDto;
import interaction_api.feign.warehouse.model.*;
import interaction_api.feign.warehouse.model.exception.NoSpecifiedProductInWarehouseException;
import interaction_api.feign.warehouse.model.exception.SpecifiedProductAlreadyInWarehouseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse.order.OrderBooking;
import warehouse.order.OrderBookingRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;
    private final OrderBookingRepository orderBookingRepository;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    public void putNewProduct(NewProductInWarehouseRequest request) throws SpecifiedProductAlreadyInWarehouseException {
        log.info("Добавление нового товара на склад id: {}", request.getProductId());

        if (repository.existsById(request.getProductId())) {
            log.warn("Товар с id {} уже есть на складе", request.getProductId());
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с Id " + request.getProductId() + " уже есть на складе");
        }

        repository.save(mapper.fromDto(request));
        log.info("Новый товар успешно добавлен id: {}", request.getProductId());
    }

    public BookedProductsDto check(ShoppingCartDto shoppingCartDto) throws NoSpecifiedProductInWarehouseException {
        log.info("Проверка корзины с id {}", shoppingCartDto.getCartId());

        Map<UUID, Integer> products = shoppingCartDto.getProducts();
        Map<UUID, WarehouseProduct> warehouseItems = getWarehouseItems(products.keySet());

        if (warehouseItems.size() != products.size()) {
            log.info("Не было найдено {} товаров", products.size() - warehouseItems.size());
            throw new NoSpecifiedProductInWarehouseException("Некоторые товары не были найдены на складе");
        }

        BookedProductsDto result = calculateBookedProducts(products, warehouseItems, false);
        log.info("Проверка корзины с id: {} выполнена успешно вес: {}, объем: {}, хрупкость: {}",
                shoppingCartDto.getCartId(), result.getDeliveryWeight(), result.getDeliveryVolume(), result.getIsFragile());
        return result;
    }

    public AddressDto getAddress() {
        log.info("Запрос адреса склада");
        return new AddressDto(CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS);
    }

    public void addProduct(AddProductToWarehouseRequest request) throws NoSpecifiedProductInWarehouseException {
        log.info("Пополнение товара с id: {}", request.getProductId());

        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Товар с id " + request.getProductId() + " на складе не найден"));

        product.setQuantity(product.getQuantity() + request.getQuantity());
        log.info("Пополнение товара с id {} выполнено успешно, текущее количество: {}",
                product.getProductId(), product.getQuantity());
    }

    @Transactional
    public void acceptReturn(Map<UUID, Integer> products) {
        products.forEach((productId, quantity) -> {
            WarehouseProduct product = getProductOrThrow(productId);
            log.info("Количество продукта {} установлено {}", product, quantity);
            product.setQuantity(product.getQuantity() + quantity);
        });
    }

    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ id: %s не найден".formatted(request.getOrderId())));
        log.info("Заказ {} отправлен на доставку", orderBooking);
        orderBooking.setDeliveryId(request.getDeliveryId());
    }

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Integer> products = request.getProducts();
        Map<UUID, WarehouseProduct> warehouseItems = getWarehouseItems(products.keySet());

        validateProductsExist(products, warehouseItems);
        validateQuantity(products, warehouseItems);

        warehouseItems.forEach((productId, item) ->
                item.setQuantity(item.getQuantity() - products.get(productId)));

        orderBookingRepository.save(OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(products)
                .build());

        log.info("Товары {} собраны для заказа {}", products, request.getOrderId());
        return calculateBookedProducts(products, warehouseItems, true);
    }

    private Map<UUID, WarehouseProduct> getWarehouseItems(Set<UUID> productIds) {
        return repository.findAllById(productIds).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
    }

    private void validateProductsExist(Map<UUID, Integer> products, Map<UUID, WarehouseProduct> warehouseItems) {
        products.keySet().forEach(productId -> {
            if (!warehouseItems.containsKey(productId)) {
                throw new NoSpecifiedProductInWarehouseException(
                        "Товар id: %s не найден".formatted(productId));
            }
        });
    }

    private void validateQuantity(Map<UUID, Integer> products, Map<UUID, WarehouseProduct> warehouseItems) {
        products.forEach((productId, quantity) -> {
            if (warehouseItems.get(productId).getQuantity() < quantity) {
                throw new LowQuantityException(
                        "Товара id: %s осталось мало: %d".formatted(productId, quantity));
            }
        });
    }

    private BookedProductsDto calculateBookedProducts(Map<? extends UUID, ? extends Number> products,
                                                      Map<UUID, WarehouseProduct> warehouseItems, boolean logResult) {
        double weight = 0;
        double volume = 0;
        boolean isFragile = false;

        for (Map.Entry<? extends UUID, ? extends Number> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            WarehouseProduct product = warehouseItems.get(productId);
            double quantity = entry.getValue().doubleValue();

            weight += product.getWeight() * quantity;
            volume += product.getWidth() * product.getHeight() * product.getDepth() * quantity;
            isFragile = isFragile || product.getFragile();
        }

        if (logResult) {
            log.info("Зарезервированный товар имеет характеристики: weight {}, volume: {}, fragile: {}", weight, volume, isFragile);
        }

        return BookedProductsDto.builder()
                .deliveryWeight(weight)
                .deliveryVolume(volume)
                .isFragile(isFragile)
                .build();
    }

    private WarehouseProduct getProductOrThrow(UUID productId) throws NoSpecifiedProductInWarehouseException {
        return repository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Товар с id: %s не найден".formatted(productId)));
    }
}
