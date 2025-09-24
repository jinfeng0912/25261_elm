package cn.edu.tju.elm.controller;

import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.service.FoodService;
import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.core.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@Tag(name = "管理商品", description = "对商品进行增删改查")
public class FoodController {
    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @GetMapping("/{id}")
    @Operation(summary = "返回查询到的一条商品记录", method = "GET")
    public HttpResult<Food> getFoodById(@PathVariable Long id){
        try {
            var food = foodService.getFoodById(id);
            if (food.isPresent()) {
                return HttpResult.success(food.get());
            } else {
                return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "商品不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取商品失败: " + e.getMessage());
        }
    }

    @GetMapping("")
    @Operation(summary = "获取所有商品", method = "GET")
    public HttpResult<List<Food>> getAllFoods(@RequestParam(name = "business", required = false) Long businessId,
                                              @RequestParam(name = "order", required = false) Long orderId){
        try {
            List<Food> foods;
            if (businessId != null) {
                // 根据商家ID获取商品
                foods = foodService.getFoodsByBusinessId(businessId);
            } else {
                // 获取所有商品
                foods = foodService.getAllFoods();
            }
            return HttpResult.success(foods);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取商品列表失败: " + e.getMessage());
        }
    }

    @PostMapping("")
    @Operation(summary = "添加商品", method = "POST")
    public HttpResult<Food> addFood(@RequestBody Food food){
        try {
            System.out.println("=== 开始处理添加商品请求 ===");
            System.out.println("请求体: " + food);
            
            // 获取当前用户
            var currentUser = userService.getUserWithAuthorities();
            System.out.println("当前用户: " + currentUser);
            
            if (currentUser.isEmpty()) {
                System.out.println("用户未登录");
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            
            // 检查必要字段
            if (food.getFoodName() == null || food.getFoodName().trim().isEmpty()) {
                System.out.println("商品名称为空");
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "商品名称不能为空");
            }
            if (food.getFoodPrice() == null) {
                System.out.println("商品价格为空");
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "商品价格不能为空");
            }
            if (food.getBusiness() == null || food.getBusiness().getId() == null) {
                System.out.println("商家ID为空");
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "商家ID不能为空");
            }
            
            System.out.println("准备调用 foodService.addFood");
            Food saved = foodService.addFood(currentUser.get(), food);
            System.out.println("保存成功: " + saved);
            return HttpResult.success(saved);
        } catch (Exception e) {
            System.out.println("异常: " + e.getMessage());
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "添加商品失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品", method = "PUT")
    public HttpResult<Food> updateFood(@PathVariable Long id, @RequestBody Food food){
        try {
            // 获取当前用户
            var currentUser = userService.getUserWithAuthorities();
            if (currentUser.isEmpty()) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            
            Food saved = foodService.updateFood(currentUser.get(), id, food);
            return HttpResult.success(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "更新商品失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", method = "DELETE")
    public HttpResult<?> deleteFood(@PathVariable Long id){
        try {
            // 获取当前用户
            var currentUser = userService.getUserWithAuthorities();
            if (currentUser.isEmpty()) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            
            foodService.deleteFood(currentUser.get(), id);
            return HttpResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "删除商品失败: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的商品", method = "GET")
    public HttpResult<List<Food>> getMyFoods(){
        try {
            // 获取当前用户
            var currentUser = userService.getUserWithAuthorities();
            if (currentUser.isEmpty()) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            
            List<Food> foods = foodService.getFoodsByUser(currentUser.get());
            return HttpResult.success(foods);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取我的商品失败: " + e.getMessage());
        }
    }
}
