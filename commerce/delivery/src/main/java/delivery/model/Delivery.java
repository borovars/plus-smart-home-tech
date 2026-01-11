package delivery.model;

import interaction_api.feign.delivery.model.DeliveryState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "order_delivery")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {

    @Id
    @UuidGenerator
    UUID id;

    @Column(name = "total_volume")
    double totalVolume;

    @Column(name = "total_weight")
    double totalWeight;

    @Column(name = "fragile")
    boolean isFragile;

    @ManyToOne
    @JoinColumn(name = "from_address_id")
    @NotNull
    Address fromAddress;

    @ManyToOne
    @JoinColumn(name = "to_address_id", nullable = false)
    Address toAddress;

    @Column(name = "order_id", nullable = false)
    UUID orderId;

    @Enumerated(EnumType.STRING)
    @NotNull
    DeliveryState deliveryState;

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", totalVolume=" + totalVolume +
                ", totalWeight=" + totalWeight +
                ", isFragile=" + isFragile +
                ", fromAddress=" + fromAddress +
                ", toAddress=" + toAddress +
                ", orderId=" + orderId +
                ", deliveryState=" + deliveryState +
                '}';
    }
}
