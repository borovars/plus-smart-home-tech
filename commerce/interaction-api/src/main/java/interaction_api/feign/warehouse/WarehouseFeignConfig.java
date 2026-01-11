package interaction_api.feign.warehouse;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "interaction_api.feign.warehouse")
public class WarehouseFeignConfig {
}
