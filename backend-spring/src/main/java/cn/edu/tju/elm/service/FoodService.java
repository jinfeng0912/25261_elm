package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.repository.FoodRepository;
import cn.edu.tju.elm.repository.BusinessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // 新增导入：用于时间戳
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;
    private final BusinessRepository businessRepository;

    public FoodService(FoodRepository foodRepository, BusinessRepository businessRepository) {
        this.foodRepository = foodRepository;
        this.businessRepository = businessRepository;
    }

    /**
     * 添加商品
     */
    public Food addFood(User user, Food food) {
        System.out.println("=== FoodService.addFood 开始 ===");
        System.out.println("用户ID: " + user.getId());
        System.out.println("商家ID: " + food.getBusiness().getId());
        System.out.println("商品名称: " + food.getFoodName());
        System.out.println("商品价格: " + food.getFoodPrice());

        // 验证商家是否存在且属于当前用户
        Business business = businessRepository.findById(food.getBusiness().getId())
                .orElseThrow(() -> {
                    System.out.println("商家不存在: " + food.getBusiness().getId());
                    return new RuntimeException("商家不存在");
                });

        System.out.println("找到商家: " + business.getBusinessName());
        System.out.println("商家老板ID: " + business.getBusinessOwner().getId());

        // 检查用户是否有权限操作该商家
        if (!business.getBusinessOwner().getId().equals(user.getId())) {
            System.out.println("权限检查失败: 用户ID " + user.getId() + " != 商家老板ID " + business.getBusinessOwner().getId());
            throw new RuntimeException("无权限操作该商家");
        }

        food.setBusiness(business);

        // 新增：填充 BaseEntity 字段（create/update 时间、用户、deleted）
        LocalDateTime now = LocalDateTime.now();
        food.setCreateTime(now);
        food.setUpdateTime(now);
        food.setCreator(user.getId());
        food.setUpdater(user.getId());
        food.setDeleted(false); // 软删除默认 false

        // 新增：填充可选字段默认值（如果前端未传或 null，设为空字符串而非 null）
        if (food.getFoodExplain() == null) {
            food.setFoodExplain("");
        }
        if (food.getFoodImg() == null) {
            food.setFoodImg("");
        }
        if (food.getRemarks() == null) {
            food.setRemarks("");
        }

        System.out.println("准备保存商品 (已填充字段): " + food);
        Food saved = foodRepository.save(food);
        System.out.println("保存成功: " + saved);
        return saved;
    }

    /**
     * 根据ID获取商品（过滤 deleted=false）
     */
    @Transactional(readOnly = true)
    public Optional<Food> getFoodById(Long id) {
        Optional<Food> foodOpt = foodRepository.findById(id);
        // 新增：过滤软删除（如果存在且 deleted=false）
        return foodOpt.filter(food -> !food.getDeleted());
    }

    /**
     * 获取所有商品（过滤 deleted=false）
     */
    @Transactional(readOnly = true)
    public List<Food> getAllFoods() {
        List<Food> allFoods = foodRepository.findAll();
        // 新增：过滤软删除
        return allFoods.stream().filter(food -> !food.getDeleted()).collect(Collectors.toList());
    }

    /**
     * 根据商家ID获取商品（过滤 deleted=false）
     */
    @Transactional(readOnly = true)
    public List<Food> getFoodsByBusinessId(Long businessId) {
        List<Food> foods = foodRepository.findByBusinessId(businessId);
        // 新增：过滤软删除
        return foods.stream().filter(food -> !food.getDeleted()).collect(Collectors.toList());
    }

    /**
     * 更新商品
     */
    public Food updateFood(User user, Long foodId, Food updateFood) {
        Food existingFood = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查用户是否有权限操作该商品
        if (!existingFood.getBusiness().getBusinessOwner().getId().equals(user.getId())) {
            throw new RuntimeException("无权限操作该商品");
        }

        // 更新字段（原有逻辑保持：只更新传的字段）
        if (updateFood.getFoodName() != null) {
            existingFood.setFoodName(updateFood.getFoodName());
        }
        if (updateFood.getFoodExplain() != null) {
            existingFood.setFoodExplain(updateFood.getFoodExplain());
        }
        if (updateFood.getFoodImg() != null) {
            existingFood.setFoodImg(updateFood.getFoodImg());
        }
        if (updateFood.getFoodPrice() != null) {
            existingFood.setFoodPrice(updateFood.getFoodPrice());
        }
        if (updateFood.getRemarks() != null) {
            existingFood.setRemarks(updateFood.getRemarks());
        }

        // 新增：填充 updateTime/updater（creator 保持不变）
        existingFood.setUpdateTime(LocalDateTime.now());
        existingFood.setUpdater(user.getId());

        return foodRepository.save(existingFood);
    }

    /**
     * 删除商品（改为软删除：set deleted=true + updateTime/updater）
     */
    public void deleteFood(User user, Long foodId) {
        System.out.println("=== FoodService.deleteFood 开始: ID=" + foodId + " ===");
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查用户是否有权限操作该商品
        if (!food.getBusiness().getBusinessOwner().getId().equals(user.getId())) {
            throw new RuntimeException("无权限操作该商品");
        }

        // 新增：软删除（非物理删除）
        LocalDateTime now = LocalDateTime.now();
        food.setDeleted(true);
        food.setUpdateTime(now);
        food.setUpdater(user.getId());

        foodRepository.save(food); // 保存 deleted=true
        System.out.println("软删除成功: ID=" + foodId + ", 更新时间=" + now);
    }

    /**
     * 获取用户的所有商品（过滤 deleted=false）
     */
    @Transactional(readOnly = true)
    public List<Food> getFoodsByUser(User user) {
        List<Business> userBusinesses = businessRepository.findByBusinessOwnerId(user.getId());
        // 原有逻辑：流式查询每个店铺食品
        List<Food> allFoods = userBusinesses.stream()
                .flatMap(business -> foodRepository.findByBusinessId(business.getId()).stream())
                .collect(Collectors.toList());
        // 新增：过滤软删除
        return allFoods.stream().filter(food -> !food.getDeleted()).collect(Collectors.toList());
    }
}