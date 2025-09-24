package cn.edu.tju.elm.controller;

import cn.edu.tju.elm.model.Cart;
import cn.edu.tju.elm.service.CartService;
import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.core.security.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "管理购物车", description = "对购物车内的商品增删改查")
public class CartController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @PostMapping("/carts")
    public HttpResult<Cart>  addCartItem(@RequestBody Cart cart){
        try {
            // 获取当前用户
            var currentUser = userService.getUserWithAuthorities();
            if (currentUser.isEmpty()) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            
            // 检查必要字段
            if (cart.getFood() == null || cart.getFood().getId() == null) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "商品ID不能为空");
            }
            
            Cart saved = cartService.addToCart(
                    currentUser.get(),
                    cart.getFood().getId(),
                    cart.getQuantity()
            );
            return HttpResult.success(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "添加购物车失败: " + e.getMessage());
        }
    }

    @GetMapping("/carts")
    public HttpResult<?> listMyCarts(){
        try {
            var currentUser = userService.getUserWithAuthorities();
            if (currentUser.isEmpty()) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            return HttpResult.success(cartService.listMyCart(currentUser.get()));
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "获取购物车失败: " + e.getMessage());
        }
    }

    @PutMapping("/carts/{id}")
    public HttpResult<Cart> updateCartItem(@PathVariable Long id, @RequestBody Cart update){
        try {
            var currentUser = userService.getUserWithAuthorities();
            if (currentUser.isEmpty()) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            Cart saved = cartService.updateQuantity(currentUser.get(), id, update.getQuantity());
            return HttpResult.success(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "更新购物车失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/carts/{id}")
    public HttpResult<?> deleteCartItem(@PathVariable Long id){
        try {
            var currentUser = userService.getUserWithAuthorities();
            if (currentUser.isEmpty()) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "用户未登录");
            }
            cartService.removeItem(currentUser.get(), id);
            return HttpResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "删除购物车失败: " + e.getMessage());
        }
    }
}
