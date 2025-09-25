package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.repository.BusinessRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    // jinfeng 新增这个方法,businesspanel需要用到
    @Transactional(readOnly = true)
    public List<Business> getBusinessesByUser(User user) {
        return businessRepository.findByBusinessOwnerId(user.getId());
    }
}