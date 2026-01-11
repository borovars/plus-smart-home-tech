package interaction_api.feign.delivery;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"interaction_api.feign.delivery", "interaction_api.feign.order"})
public class DeliveryFeignConfig {
}
