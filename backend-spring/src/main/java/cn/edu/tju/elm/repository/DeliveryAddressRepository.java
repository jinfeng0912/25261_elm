package cn.edu.tju.elm.repository;

import cn.edu.tju.elm.model.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    /**
     * 根据用户ID查找所有未删除的配送地址
     */
    List<DeliveryAddress> findByCustomerIdAndDeletedFalse(Long customerId);

    /**
     * 根据用户ID和地址ID查找未删除的配送地址
     */
    Optional<DeliveryAddress> findByCustomerIdAndIdAndDeletedFalse(Long customerId, Long id);

    /**
     * 根据ID查找未删除的配送地址
     */
    Optional<DeliveryAddress> findByIdAndDeletedFalse(Long id);

    // 保留原有方法以兼容现有代码
    /**
     * 根据用户ID查找所有配送地址（包括已删除的）
     */
    List<DeliveryAddress> findByCustomerId(Long customerId);

    /**
     * 根据用户ID和地址ID查找配送地址（包括已删除的）
     */
    Optional<DeliveryAddress> findByCustomerIdAndId(Long customerId, Long id);
}