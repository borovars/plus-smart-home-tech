package interaction_api.feign.cb;

import interaction_api.common.exception.ServiceFallBackException;
import interaction_api.feign.cart.CartFeignClient;
import interaction_api.feign.cart.model.ChangeProductQuantityRequest;
import interaction_api.feign.cart.model.ShoppingCartDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static interaction_api.common.FallbackUtil.FALLBACK_MESSAGE;

@Component
public class CartClientFallback implements CartFeignClient {

    @Override
    public ShoppingCartDto get(String userName) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public ShoppingCartDto add(String userName, Map<UUID, Long> products) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public void deleteCart(String userName) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public ShoppingCartDto removeProducts(String userName, List<UUID> productList) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public ShoppingCartDto quantityUpdate(String userName, ChangeProductQuantityRequest request) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }
}