package interaction_api.feign.cb;

import interaction_api.common.exception.ServiceFallBackException;
import interaction_api.feign.store.StoreFeignClient;
import interaction_api.feign.store.model.ProductDto;
import interaction_api.feign.store.model.SetProductQuantitySetRequest;
import interaction_api.feign.store.model.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static interaction_api.common.FallbackUtil.FALLBACK_MESSAGE;

@Component
public class StoreClientFallback implements StoreFeignClient {

    @Override
    public Page<ProductDto> getByCategory(ProductCategory productCategory, Pageable pageable) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public ProductDto add(ProductDto productDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public Boolean delete(UUID id) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public Boolean quantityUpdate(SetProductQuantitySetRequest setProductQuantitySetRequest) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }

    @Override
    public ProductDto getById(UUID productId) {
        throw new ServiceFallBackException(FALLBACK_MESSAGE.toString());
    }
}
