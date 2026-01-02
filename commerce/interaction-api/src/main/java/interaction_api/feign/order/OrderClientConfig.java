package interaction_api.feign.order;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"interaction_api.feign.order", "interaction_api.feign.payment",
        "interaction_api.feign.delivery", "interaction_api.feign.warehouse"})
public class OrderClientConfig {
}
