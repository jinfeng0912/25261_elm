package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.repository.UserRepository;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.model.DeliveryAddress;
import cn.edu.tju.elm.model.Order;
import cn.edu.tju.elm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserService userService;

    /**
     * 新增订单
     * @param order
     * @return
     */
    @Transactional
    public Order createOrder(Order order) {
//        Long customerId = order.getCustomer().getId();
        Long businessId = order.getBusiness().getId();
        Long addressId = order.getDeliveryAddress().getId();

        User customer = userService.getUserWithAuthorities().get();
//        User customer = userRepository.findById(customerId)
//                .orElseThrow(() -> new IllegalArgumentException("无效的用户ID: " + customerId));

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("无效的商家ID: " + businessId));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("无效的配送地址ID: " + addressId));

        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setBusiness(business);
        newOrder.setDeliveryAddress(deliveryAddress);
        newOrder.setOrderTotal(order.getOrderTotal());
        newOrder.setOrderDate(LocalDate.now());
        newOrder.setOrderState(0);
        newOrder.setCreateTime(LocalDateTime.now());
        newOrder.setUpdateTime(LocalDateTime.now());
        newOrder.setCreator(customer.getId());
        newOrder.setUpdater(customer.getId());
        newOrder.setDeleted(false);

        return orderRepository.save(newOrder);

    }
    public List<Order> listOrdersByUserId(Long userId) {
        return orderRepository.findByCustomerId(userId);
    }
}