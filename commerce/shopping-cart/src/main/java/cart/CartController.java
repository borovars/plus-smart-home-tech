package cart;

import interaction_api.feign.cart.CartFeignClient;
import interaction_api.feign.cart.model.ChangeProductQuantityRequest;
import interaction_api.feign.cart.model.ShoppingCartDto;
import interaction_api.feign.cart.model.exception.NoProductsInShoppingCartException;
import interaction_api.feign.cart.model.exception.NotAuthorizedUserException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController implements CartFeignClient {

    CartService service;

    @Override
    public ShoppingCartDto get(String username) throws NotAuthorizedUserException {
        return service.get(username);
    }

    @Override
    public ShoppingCartDto add(String username, Map<UUID, Long> products) throws NotAuthorizedUserException {
        return service.add(username, products);
    }

    @Override
    public void deleteCart(String username) throws NotAuthorizedUserException {
        service.deleteCart(username);
    }

    @Override
    public ShoppingCartDto removeProducts(String username, List<UUID> productIds) throws NotAuthorizedUserException {
        return service.removeProducts(username, productIds);
    }

    @Override
    public ShoppingCartDto quantityUpdate(String username, ChangeProductQuantityRequest request) throws NoProductsInShoppingCartException, NotAuthorizedUserException {
        return service.changeQuantity(username, request);
    }
}
