package cn.edu.tju.elm.repository;

import cn.edu.tju.elm.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    // 新增：商家订单基查询 (过滤 deleted=false)
    @Query("SELECT o FROM Order o WHERE o.business.id = :businessId AND o.deleted = false ORDER BY o.createTime DESC")
    List<Order> findByBusinessIdAndDeletedFalse(@Param("businessId") Long businessId);

    // 新增：商家订单 + 状态过滤
    @Query("SELECT o FROM Order o WHERE o.business.id = :businessId AND o.orderState = :orderState AND o.deleted = false ORDER BY o.createTime DESC")
    List<Order> findByBusinessIdAndOrderStateAndDeletedFalse(@Param("businessId") Long businessId,
            @Param("orderState") Integer orderState);

    // 新增：商家订单 + 日期范围 + 状态 (可选，:orderState NULL 时忽略)
    @Query("SELECT o FROM Order o WHERE o.business.id = :businessId " +
            "AND o.createTime >= :startDate AND o.createTime <= :endDate " +
            "AND o.deleted = false " +
            "AND (:orderState IS NULL OR o.orderState = :orderState) " +
            "ORDER BY o.createTime DESC")
    List<Order> findByBusinessIdAndDateRangeAndStatus(
            @Param("businessId") Long businessId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("orderState") Integer orderState);

    // 新增：商家订单计数
    @Query("SELECT COUNT(o) FROM Order o WHERE o.business.id = :businessId AND o.deleted = false")
    long countByBusinessIdAndDeletedFalse(@Param("businessId") Long businessId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.business.id = :businessId AND o.orderState = :orderState AND o.deleted = false")
    long countByBusinessIdAndOrderStateAndDeletedFalse(@Param("businessId") Long businessId,
            @Param("orderState") Integer orderState);

    // 新增：单个订单详情 + deleted=false (通用，用户/商家共享)
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.deleted = false")
    Optional<Order> findByIdAndDeletedFalse(@Param("id") Long id);
}