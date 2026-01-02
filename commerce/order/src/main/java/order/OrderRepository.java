package order;

import java.util.List;
import java.util.UUID;
import order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUserName(String userName);
}
