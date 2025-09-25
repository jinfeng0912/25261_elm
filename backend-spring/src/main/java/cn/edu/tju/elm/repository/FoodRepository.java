package cn.edu.tju.elm.repository;

import cn.edu.tju.elm.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    // 原有：按 business_id 查
    List<Food> findByBusinessId(Long businessId);

    // 新增：按 business_id + deleted=false（过滤软删除）
    @Query("SELECT f FROM Food f WHERE f.business.id = :businessId AND f.deleted = false")
    List<Food> findByBusinessIdAndDeletedFalse(@Param("businessId") Long businessId);

    // 新增：所有未删除食品
    List<Food> findAllByDeletedFalse();

    // 新增：单个 ID + deleted=false
    @Query("SELECT f FROM Food f WHERE f.id = :id AND f.deleted = false")
    Optional<Food> findByIdAndDeletedFalse(@Param("id") Long id);

    // 可选：按用户 ID 查（如果 getFoodsByUser 需优化，但你的流式 OK，我保留原有）
    @Query("SELECT f FROM Food f JOIN f.business b WHERE b.businessOwner.id = :userId AND f.deleted = false")
    List<Food> findByBusinessBusinessOwnerId(@Param("userId") Long userId);
}