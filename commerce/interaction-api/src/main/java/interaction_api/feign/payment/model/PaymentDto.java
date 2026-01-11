package interaction_api.feign.payment.model;


import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentDto {

    private UUID paymentId;

    private double totalPayment;

    private double deliveryTotal;

    private double feeTotal;
}
