package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.repository.BusinessRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // 已修正: 添加了接收 Map 类型参数的 addBusiness 方法，以匹配 Controller 中的调用
    public Business addBusiness(Map<String, Object> payload) {
        Business business = new Business();

        // 1. 从 Map 手动映射客户端可以提供的数据
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

        // 2. 由服务器强制设置应由服务器管理的字段，忽略客户端传来的值
        business.setCreateTime(LocalDateTime.now());
        business.setUpdateTime(LocalDateTime.now());
        business.setDeleted(false);
        business.setId(null);

        // 3. 使用 EntityManager.getReference 建立外键关联，无需查询 User
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
        return businessRepository.findAll();
    }

    public Business updateBusiness(Long id, Business business) {
        Optional<Business> existingBusinessOpt = businessRepository.findById(id);
        if (existingBusinessOpt.isPresent() && !existingBusinessOpt.get().getDeleted()) {
            Business existingBusiness = existingBusinessOpt.get();
            // ... (此处省略了更新逻辑)
            return businessRepository.save(existingBusiness);
        }
        return null;
    }

    public Business deleteBusiness(Long id) {
        Optional<Business> existingBusinessOpt = businessRepository.findById(id);
        if (existingBusinessOpt.isPresent()) {
            Business existingBusiness = existingBusinessOpt.get();
            existingBusiness.setDeleted(true);
            existingBusiness.setUpdateTime(LocalDateTime.now());
            return businessRepository.save(existingBusiness);
        }
        return null;
    }

    public Business patchBusiness(Long id, Business business) {
        return updateBusiness(id, business);
    }
}