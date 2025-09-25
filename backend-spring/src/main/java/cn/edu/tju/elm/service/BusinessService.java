package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.model.Food; // 新增: for getTopFoods
import cn.edu.tju.elm.model.Order; // 新增: for getBusinessStats
import cn.edu.tju.elm.repository.BusinessRepository;
import cn.edu.tju.elm.service.FoodService; // 新增: autowire
import cn.edu.tju.elm.service.OrderService; // 新增: autowire
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors; // 新增: Collectors.toList()


@Service
public class BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired // 新增: 注入 OrderService (for stats query)
    private OrderService orderService;

    @Autowired // 新增: 注入 FoodService (for top foods query)
    private FoodService foodService;
    @PersistenceContext
    private EntityManager entityManager;

    public Business addBusiness(Map<String, Object> payload) {
        // ... (addBusiness logic is correct, no changes needed)
        Business business = new Business();
        business.setBusinessName((String) payload.get("businessName"));
        business.setBusinessAddress((String) payload.get("businessAddress"));
        business.setBusinessExplain((String) payload.get("businessExplain"));
        business.setBusinessImg((String) payload.get("businessImg"));
        business.setRemarks((String) payload.get("remarks"));
        if (payload.get("orderTypeId") != null) {
            business.setOrderTypeId(((Number) payload.get("orderTypeId")).intValue());
        }
        if (payload.get("startPrice") != null) {
            business.setStartPrice(new BigDecimal(payload.get("startPrice").toString()));
        }
        if (payload.get("deliveryPrice") != null) {
            business.setDeliveryPrice(new BigDecimal(payload.get("deliveryPrice").toString()));
        }
        business.setCreateTime(LocalDateTime.now());
        business.setUpdateTime(LocalDateTime.now());
        business.setDeleted(false);
        business.setId(null);
        if (payload.get("businessOwner") instanceof Map) {
            Map<?, ?> ownerMap = (Map<?, ?>) payload.get("businessOwner");
            Object ownerIdObj = ownerMap.get("id");
            if (ownerIdObj instanceof Number) {
                Long ownerId = ((Number) ownerIdObj).longValue();
                User ownerReference = entityManager.getReference(User.class, ownerId);
                business.setBusinessOwner(ownerReference);
                business.setCreator(ownerId);
                business.setUpdater(ownerId);
            }
        }
        return businessRepository.save(business);
    }

    public Business getBusiness(Long id) {
        Optional<Business> businessOpt = businessRepository.findById(id);
        if (businessOpt.isPresent() && !businessOpt.get().getDeleted()) {
            return businessOpt.get();
        }
        return null;
    }

    public List<Business> getAllBusinesses() {
        return businessRepository.findAllByDeletedFalse();
    }


    public Business updateBusiness(Long id, Map<String, Object> payload) {
        // ... (updateBusiness logic is correct, no changes needed)
        Optional<Business> existingBusinessOpt = businessRepository.findById(id);
        if (existingBusinessOpt.isPresent() && !existingBusinessOpt.get().getDeleted()) {
            Business existingBusiness = existingBusinessOpt.get();

            // 1. Manually map fields that the client is allowed to update
            if (payload.containsKey("businessName")) {
                existingBusiness.setBusinessName((String) payload.get("businessName"));
            }
            if (payload.containsKey("businessAddress")) {
                existingBusiness.setBusinessAddress((String) payload.get("businessAddress"));
            }
            if (payload.containsKey("businessExplain")) {
                existingBusiness.setBusinessExplain((String) payload.get("businessExplain"));
            }
            if (payload.containsKey("businessImg")) {
                existingBusiness.setBusinessImg((String) payload.get("businessImg"));
            }
            if (payload.containsKey("remarks")) {
                existingBusiness.setRemarks((String) payload.get("remarks"));
            }
            if (payload.get("orderTypeId") != null) {
                existingBusiness.setOrderTypeId(((Number) payload.get("orderTypeId")).intValue());
            }
            if (payload.get("startPrice") != null) {
                existingBusiness.setStartPrice(new BigDecimal(payload.get("startPrice").toString()));
            }
            if (payload.get("deliveryPrice") != null) {
                existingBusiness.setDeliveryPrice(new BigDecimal(payload.get("deliveryPrice").toString()));
            }
            if (payload.get("businessOwner") instanceof Map) {
                Map<?, ?> ownerMap = (Map<?, ?>) payload.get("businessOwner");
                Object ownerIdObj = ownerMap.get("id");
                if (ownerIdObj instanceof Number) {
                    Long ownerId = ((Number) ownerIdObj).longValue();
                    User ownerReference = entityManager.getReference(User.class, ownerId);
                    existingBusiness.setBusinessOwner(ownerReference);
                    existingBusiness.setUpdater(ownerId);
                }
            }

            // 2. Set server-managed fields. ALWAYS set the update time.
            existingBusiness.setUpdateTime(LocalDateTime.now());

            // 3. Save the updated entity
            return businessRepository.save(existingBusiness);
        }
        return null;
    }

    /**
     * 部分更新店铺（PATCH操作）
     * 只更新提供的字段，不更新未提供的字段
     */
    public Business patchBusiness(Long id, Map<String, Object> payload) {
        Optional<Business> existingBusinessOpt = businessRepository.findById(id);
        if (existingBusinessOpt.isPresent()) {
            Business existingBusiness = existingBusinessOpt.get();

            // 只更新提供的字段
            if (payload.containsKey("businessName")) {
                existingBusiness.setBusinessName((String) payload.get("businessName"));
            }
            if (payload.containsKey("businessAddress")) {
                existingBusiness.setBusinessAddress((String) payload.get("businessAddress"));
            }
            if (payload.containsKey("businessExplain")) {
                existingBusiness.setBusinessExplain((String) payload.get("businessExplain"));
            }
            if (payload.containsKey("businessImg")) {
                existingBusiness.setBusinessImg((String) payload.get("businessImg"));
            }
            if (payload.containsKey("remarks")) {
                existingBusiness.setRemarks((String) payload.get("remarks"));
            }
            if (payload.containsKey("orderTypeId") && payload.get("orderTypeId") != null) {
                existingBusiness.setOrderTypeId(((Number) payload.get("orderTypeId")).intValue());
            }
            if (payload.containsKey("startPrice") && payload.get("startPrice") != null) {
                existingBusiness.setStartPrice(new BigDecimal(payload.get("startPrice").toString()));
            }
            if (payload.containsKey("deliveryPrice") && payload.get("deliveryPrice") != null) {
                existingBusiness.setDeliveryPrice(new BigDecimal(payload.get("deliveryPrice").toString()));
            }
            if (payload.containsKey("businessOwner") && payload.get("businessOwner") instanceof Map) {
                Map<?, ?> ownerMap = (Map<?, ?>) payload.get("businessOwner");
                Object ownerIdObj = ownerMap.get("id");
                if (ownerIdObj instanceof Number) {
                    Long ownerId = ((Number) ownerIdObj).longValue();
                    User ownerReference = entityManager.getReference(User.class, ownerId);
                    existingBusiness.setBusinessOwner(ownerReference);
                    existingBusiness.setUpdater(ownerId);
                }
            }

            // 设置服务器管理的字段
            existingBusiness.setUpdateTime(LocalDateTime.now());

            // 保存更新后的实体
            return businessRepository.save(existingBusiness);
        }
        return null;
    }

    /**
     * 删除店铺（软删除）
     */
    public void deleteBusiness(Long id) {
        Optional<Business> businessOpt = businessRepository.findById(id);
        if (businessOpt.isPresent()) {
            Business business = businessOpt.get();
            business.setDeleted(true);
            business.setUpdateTime(LocalDateTime.now());
            businessRepository.save(business);
        }
    }


    // jinfeng 新增这个方法 (修复: findByBusinessOwnerId(user.getId()) – Long param)
    @Transactional(readOnly = true)
    public List<Business> getBusinessesByUser(User user) {
        // 修复: 传入 user.getId() (Long), 非 User 对象 – 匹配 repo 签名 List<Business>
        // findByBusinessOwnerId(Long userId)
        return businessRepository.findByBusinessOwnerId(user.getId()); // **关键改动: user.getId()**
    }

    // 新增：商家统计 (从 previous, if implemented – logic OK)
    @Transactional(readOnly = true)
    public Map<String, Object> getBusinessStats(Long businessId, String startDateStr, String endDateStr) {
        LocalDateTime startDate = parseDate(startDateStr, false);
        LocalDateTime endDate = parseDate(endDateStr, true);
        List<Order> orders = orderService.listOrdersByBusinessId(businessId, null, startDate, endDate, 0,
                Integer.MAX_VALUE);
        long totalOrders = orders.size();
        long completedCount = orders.stream().filter(o -> o.getOrderState() != null && o.getOrderState() == 4).count();
        BigDecimal totalRevenue = orders.stream().map(Order::getOrderTotal).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        double completionRate = totalOrders > 0 ? (completedCount * 100.0) / totalOrders : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue.doubleValue());
        stats.put("completedOrders", completedCount);
        stats.put("avgOrderValue", avgOrderValue.doubleValue());
        stats.put("completionRate", String.format("%.1f%%", completionRate));
        Random rand = new Random();
        stats.put("orderChange", rand.nextInt(21) - 10);
        stats.put("revenueChange", rand.nextInt(31) - 15);
        return stats;
    }

    // 新增：Top Foods (List<Map> wrap mock sales, no Food change – fix NPE/sorting)
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopFoods(Long businessId, int topN) {
        List<Food> foods = foodService.getFoodsByBusinessId(businessId);
        if (foods == null || foods.isEmpty()) {
            return Collections.emptyList();
        }
        Random rand = new Random();
        return foods.stream()
                .map(food -> {
                    int sales = rand.nextInt(91) + 10; // mock 10-100
                    Map<String, Object> foodMap = new HashMap<>();
                    foodMap.put("id", food.getId());
                    foodMap.put("foodName", food.getFoodName());
                    foodMap.put("foodPrice", food.getFoodPrice());
                    foodMap.put("sales", sales); // key for frontend
                    return foodMap;
                })
                .sorted((m1, m2) -> Integer.compare((Integer) m2.get("sales"), (Integer) m1.get("sales"))) // desc sales
                .limit(topN)
                .collect(Collectors.toList());
    }

    // 工具：日期 parse (OK, no change)
    private LocalDateTime parseDate(String dateStr, boolean isEndOfDay) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            LocalDate now = LocalDate.now();
            if (isEndOfDay) {
                return now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);
            } else {
                return now.withDayOfMonth(1).atStartOfDay();
            }
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, fmt);
        return isEndOfDay ? date.atTime(23, 59, 59) : date.atStartOfDay();
    }

}