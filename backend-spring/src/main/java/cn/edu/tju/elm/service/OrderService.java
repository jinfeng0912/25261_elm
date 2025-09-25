package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.repository.UserRepository;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.model.DeliveryAddress;
import cn.edu.tju.elm.model.Order;
import cn.edu.tju.elm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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
    
    @Transactional
    public Order createOrder(Order order) {
        Long businessId = order.getBusiness().getId();
        Long addressId = order.getDeliveryAddress().getId();

        User customer = userService.getUserWithAuthorities().get();

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
        List<Order> orders = orderRepository.findByCustomerId(userId);
        return orders.stream()
                .filter(order -> !order.getDeleted()) // 加软删除过滤 (不影响原有行为)
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Order markPaid(Long orderId) {
        Order order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("无效的订单ID: " + orderId));
        order.setOrderState(1);
        order.setUpdateTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    // jinfeng新增：商家订单列表 (用于新端点 /business/{id})
    @Transactional(readOnly = true)
    public List<Order> listOrdersByBusinessId(Long businessId, Integer orderState, LocalDateTime startDate,
            LocalDateTime endDate, int page, int size) {
        List<Order> orders;
        if (orderState != null && startDate != null && endDate != null) {
            orders = orderRepository.findByBusinessIdAndDateRangeAndStatus(businessId, startDate, endDate, orderState);
        } else if (orderState != null) {
            orders = orderRepository.findByBusinessIdAndOrderStateAndDeletedFalse(businessId, orderState);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByBusinessIdAndDateRangeAndStatus(businessId, startDate, endDate, null);
        } else {
            orders = orderRepository.findByBusinessIdAndDeletedFalse(businessId);
        }

        // 分页 (简单, 生产用 Pageable)
        int from = page * size;
        int to = Math.min(from + size, orders.size());
        if (from >= orders.size())
            return List.of(); // 空页
        return orders.subList(from, to);
    }

    // jinfeng新增：商家订单计数 (用于 /count)
    public long countOrdersByBusinessId(Long businessId) {
        return orderRepository.countByBusinessIdAndDeletedFalse(businessId);
    }

    public long countOrdersByBusinessIdAndOrderState(Long businessId, Integer orderState) {
        return orderRepository.countByBusinessIdAndOrderStateAndDeletedFalse(businessId, orderState);
    }

    // jinfeng新增：更新订单状态 (用于新端点 /{id}/status)
    @Transactional
    public Order updateOrderStatus(Long orderId, Integer newState, User operator) {
        Order order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + orderId));

        // 状态流转验证 (简易)
        if (newState == 5 && order.getOrderState() > 2) { // 取消只限前3状态
            throw new IllegalStateException("该状态订单不能取消");
        }
        if (newState == 2 && order.getOrderState() != 1) { // 接单只从已支付
            throw new IllegalStateException("只能从已支付状态接单");
        }
        // ... 其他验证类似 (可扩展)

        order.setOrderState(newState);
        order.setUpdateTime(LocalDateTime.now());
        order.setUpdater(operator.getId());
        return orderRepository.save(order);
    }

    // jinfeng新增：订单详情 (用于现有 GET /{id}, 但加过滤 – 兼容)
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在: " + id));
    }

    // jinfeng新增：订单统计 (used by BusinessService)
    @Transactional(readOnly = true)
    public List<Order> getOrderStats(Long businessId, LocalDateTime startDate, LocalDateTime endDate) {
        return listOrdersByBusinessId(businessId, 4, startDate, endDate, 0, Integer.MAX_VALUE); // all 完成订单 (state=4),
                                                                                                // no page for sum
    }


}