package cn.edu.tju.elm.repository;

import cn.edu.tju.elm.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerIdAndFoodId(Long customerId, Long foodId);

    List<Cart> findByCustomerId(Long customerId);

    List<Cart> findByCustomerIdAndBusinessId(Long customerId, Long businessId);
}