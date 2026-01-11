package interaction_api.feign.cart;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "interaction_api.feign.cart")
public class CartFeignConfig {
}
