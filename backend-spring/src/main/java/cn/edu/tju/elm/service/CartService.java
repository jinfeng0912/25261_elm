package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Cart;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.repository.CartRepository;
import cn.edu.tju.elm.repository.FoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final FoodRepository foodRepository;

    public CartService(CartRepository cartRepository, FoodRepository foodRepository) {
        this.cartRepository = cartRepository;
        this.foodRepository = foodRepository;
    }

    public Cart addToCart(User user, Long foodId, Integer quantity) {
        Food food = foodRepository.findById(foodId).orElseThrow(() -> 
            new RuntimeException("商品不存在: " + foodId)
        );
        
        Cart cart = cartRepository.findByCustomerIdAndFoodId(user.getId(), foodId).orElseGet(Cart::new);
        cart.setCustomer(user);
        cart.setFood(food);
        cart.setBusiness(food.getBusiness());
        int base = cart.getQuantity() == null ? 0 : cart.getQuantity();
        cart.setQuantity(base + (quantity == null ? 1 : quantity));
        
        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public List<Cart> listMyCart(User user) {
        return cartRepository.findByCustomerId(user.getId());
    }

    public Cart updateQuantity(User user, Long cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        if (!cart.getCustomer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权操作该购物车项");
        }
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    public void removeItem(User user, Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        if (!cart.getCustomer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权操作该购物车项");
        }
        cartRepository.deleteById(cartId);
    }
}