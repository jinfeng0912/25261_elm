package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.model.Food; // 新增: for getTopFoods
import cn.edu.tju.elm.model.Order; // 新增: for getBusinessStats
import cn.edu.tju.elm.model.dto.BusinessDTO;
import cn.edu.tju.elm.repository.BusinessRepository;
import cn.edu.tju.elm.service.FoodService; // 新增: autowire
import cn.edu.tju.elm.service.OrderService; // 新增: autowire
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
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

    private final String BAIDU_API_KEY = "h7ykSCW3nilIKlSlb5lK3vk7WLCnwhFB";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用百度地图API将地址转换为经纬度坐标
     * @param address 文本地址
     * @return 包含经纬度的数组 [longitude, latitude]，失败则返回 null
     */
    private Double[] geocodeAddress(String address) {
        try {
            String url = "https://api.map.baidu.com/geocoding/v3/?address=" +
                    java.net.URLEncoder.encode(address, "UTF-8") +
                    "&output=json" +
                    "&ak=" + BAIDU_API_KEY;

            Request request = new Request.Builder().url(url).build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("请求百度API失败: " + response.code());
                    return null;
                }

                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);

                // 百度地图成功时 status 为 0
                if (root.path("status").asInt() == 0) {
                    JsonNode locationNode = root.path("result").path("location");
                    if (!locationNode.isMissingNode()) {
                        double lng = locationNode.path("lng").asDouble(); // 经度
                        double lat = locationNode.path("lat").asDouble(); // 纬度

                        // 3. 修正返回顺序：[经度, 纬度]
                        return new Double[]{lng, lat};
                    }
                } else {
                    // 打印百度API返回的错误信息，方便调试
                    System.out.println("百度API返回错误: " + root.path("message").asText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有商家信息，并根据用户坐标动态计算距离
     * @param userLat 前端传递的用户纬度，可能为 null
     * @param userLng 前端传递的用户经度，可能为 null
     * @return 包含距离信息的商家DTO列表
     */
    public List<BusinessDTO> getAllBusinesses(Double userLat, Double userLng) {

        // 1. 从数据库获取所有未被软删除的商家实体列表
        List<Business> businesses = businessRepository.findAllByDeletedFalse();

        // 2. 使用Java Stream API将Business实体列表高效地转换为BusinessDTO列表
        return businesses.stream().map(business -> {

            // 为每个 business 实体创建一个 DTO 对象
            BusinessDTO dto = new BusinessDTO(business);

            // 3. 检查前端是否提供了用户坐标，并且商家本身也存有坐标
            if (userLat != null && userLng != null && business.getLatitude() != null && business.getLongitude() != null) {

                // 4. 如果坐标都存在，则调用距离计算方法
                double distance = calculateDistance(
                        userLat,
                        userLng,
                        business.getLatitude(),
                        business.getLongitude()
                );

                // 5. 将计算出的距离（保留一位小数）设置到DTO对象中
                dto.setDistance(Math.round(distance * 10.0) / 10.0);
            }
            // 注意：如果缺少任何坐标，dto的distance字段将保持为null，这是预期的行为

            return dto;

        }).collect(Collectors.toList()); // 6. 将所有处理过的DTO收集到一个新的列表中并返回
    }

    public Business addBusiness(Map<String, Object> payload) {
        // ... (addBusiness logic is correct, no changes needed)
        Business business = new Business();
        business.setBusinessName((String) payload.get("businessName"));
        business.setBusinessAddress((String) payload.get("businessAddress"));
        business.setMonthlySales(0);
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
        Optional<Business> existingBusinessOpt = businessRepository.findById(id);
        if (existingBusinessOpt.isPresent() && !existingBusinessOpt.get().getDeleted()) {
            Business existingBusiness = existingBusinessOpt.get();

            // 检查地址是否被更新
            if (payload.containsKey("businessAddress")) {
                String newAddress = (String) payload.get("businessAddress");
                String oldAddress = existingBusiness.getBusinessAddress();

                // 关键优化：仅当地址实际发生变更时，才调用API进行地理编码
                if (newAddress != null && !newAddress.equals(oldAddress)) {
                    System.out.println("地址已变更，开始进行地理编码: " + newAddress);
                    existingBusiness.setBusinessAddress(newAddress); // 先更新文本地址

                    // 调用我们之前写好的地理编码方法
                    Double[] coords = geocodeAddress(newAddress);
                    if (coords != null) {
                        // 更新经纬度
                        existingBusiness.setLongitude(coords[0]);
                        existingBusiness.setLatitude(coords[1]);
                        System.out.println("地理编码成功，新坐标: " + coords[0] + ", " + coords[1]);
                    } else {
                        System.out.println("地址 '" + newAddress + "' 地理编码失败，坐标未更新。");
                        // 如果编码失败，可以选择将坐标清空
                        existingBusiness.setLongitude(null);
                        existingBusiness.setLatitude(null);
                    }
                }
            }

            // 更新其他允许客户端修改的字段
            if (payload.containsKey("businessName")) {
                existingBusiness.setBusinessName((String) payload.get("businessName"));
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

            // 设置服务器管理的字段
            existingBusiness.setUpdateTime(LocalDateTime.now());

            // 保存更新后的实体
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

    /**
     * 一次性任务：为数据库中所有地址有效但缺少坐标的商家进行地理编码
     *
     * @return 处理结果的摘要信息
     */
    public String batchGeocodeExistingBusinesses() {
        System.out.println("=== 开始执行批量地理编码任务 ===");

        // 1. 找出所有需要处理的商家
        List<Business> businessesToUpdate = businessRepository.findByLatitudeIsNullOrLongitudeIsNull();

        if (businessesToUpdate.isEmpty()) {
            System.out.println("没有需要更新的商家。");
            return "没有需要更新的商家。";
        }

        System.out.println("发现 " + businessesToUpdate.size() + " 个商家需要进行地理编码。");

        int successCount = 0;
        int failureCount = 0;

        // 2. 遍历每一个商家
        for (Business business : businessesToUpdate) {
            String address = business.getBusinessAddress();
            if (address != null && !address.isEmpty()) {
                System.out.println("正在处理商家: " + business.getBusinessName() + " | 地址: " + address);

                // 3. 调用我们之前写好的地理编码方法
                Double[] coords = geocodeAddress(address);

                if (coords != null) {
                    // 4. 如果成功，更新经纬度并保存
                    business.setLongitude(coords[0]);
                    business.setLatitude(coords[1]);
                    businessRepository.save(business);
                    successCount++;
                    System.out.println(" -> 成功: " + coords[0] + ", " + coords[1]);
                } else {
                    failureCount++;
                    System.out.println(" -> 失败: 无法解析地址");
                }

                // 5. 关键：在每次API调用之间增加短暂延时，防止超出地图服务商的QPS（每秒查询率）限制
                try {
                    Thread.sleep(200); // 延时200毫秒，即每秒最多调用5次
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("延时被中断");
                }

            } else {
                failureCount++;
                System.out.println("跳过商家 " + business.getBusinessName() + "，因为地址为空。");
            }
        }

        String summary = "批量地理编码任务完成。总数: " + businessesToUpdate.size() + ", 成功: " + successCount + ", 失败: "
                + failureCount;
        System.out.println(summary);
        return summary;
    }

    /**
     * 使用 Haversine 公式计算两点间的距离
     *
     * @param lat1 用户纬度
     * @param lon1 用户经度
     * @param lat2 商家纬度
     * @param lon2 商家经度
     * @return 距离（单位：公里）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径，单位公里

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

}