package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.repository.FoodRepository;
import cn.edu.tju.elm.repository.BusinessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        System.out.println("准备保存商品: " + food);
        Food saved = foodRepository.save(food);
        System.out.println("保存成功: " + saved);
        return saved;
    }

    /**
     * 根据ID获取商品
     */
    @Transactional(readOnly = true)
    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    /**
     * 获取所有商品
     */
    @Transactional(readOnly = true)
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    /**
     * 根据商家ID获取商品
     */
    @Transactional(readOnly = true)
    public List<Food> getFoodsByBusinessId(Long businessId) {
        return foodRepository.findByBusinessId(businessId);
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
        
        // 更新字段
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
        
        return foodRepository.save(existingFood);
    }

    /**
     * 删除商品
     */
    public void deleteFood(User user, Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 检查用户是否有权限操作该商品
        if (!food.getBusiness().getBusinessOwner().getId().equals(user.getId())) {
            throw new RuntimeException("无权限操作该商品");
        }
        
        foodRepository.deleteById(foodId);
    }

    /**
     * 获取用户的所有商品
     */
    @Transactional(readOnly = true)
    public List<Food> getFoodsByUser(User user) {
        List<Business> userBusinesses = businessRepository.findByBusinessOwnerId(user.getId());
        return userBusinesses.stream()
                .flatMap(business -> foodRepository.findByBusinessId(business.getId()).stream())
                .toList();
    }
}