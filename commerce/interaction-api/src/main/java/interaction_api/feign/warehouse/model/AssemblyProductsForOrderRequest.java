package interaction_api.feign.warehouse.model;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AssemblyProductsForOrderRequest {

    @NotNull
    private Map<UUID, Integer> products;

    @NotNull
    private UUID orderId;
}