package cn.edu.tju.elm.model;

import cn.edu.tju.core.model.BaseEntity;
import cn.edu.tju.core.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "business")
public class Business extends BaseEntity {

    @Column(name = "business_name")
    private String businessName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_owner_id")
    private User businessOwner;

    @Column(name = "business_address")
    private String businessAddress;

    @Column(name = "business_explain")
    private String businessExplain;

    @Column(name = "business_img")
    private String businessImg;

    @Column(name = "order_type_id")
    private Integer orderTypeId;

    @Column(name = "start_price")
    private BigDecimal startPrice;

    @Column(name = "delivery_price")
    private BigDecimal deliveryPrice;

    @Column(name = "remarks")
    private String remarks;

    // 隐藏原始的 getter 方法，防止 JSON 序列化时产生无限循环
    @JsonIgnore
    public User getBusinessOwner() {
        return businessOwner;
    }

    // 提供一个“安全”的 getter 方法用于 JSON 序列化，只包含必要信息
    @JsonProperty("businessOwner")
    public Map<String, Object> getSafeBusinessOwner() {
        if (businessOwner == null) {
            return null;
        }
        Map<String, Object> ownerMap = new HashMap<>();
        ownerMap.put("id", businessOwner.getId());
        ownerMap.put("username", businessOwner.getUsername());
        return ownerMap;
    }

    public void setBusinessOwner(User businessOwner) {
        this.businessOwner = businessOwner;
    }

    // Standard Getters and Setters
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getBusinessExplain() { return businessExplain; }
    public void setBusinessExplain(String businessExplain) { this.businessExplain = businessExplain; }
    public String getBusinessImg() { return businessImg; }
    public void setBusinessImg(String businessImg) { this.businessImg = businessImg; }
    public Integer getOrderTypeId() { return orderTypeId; }
    public void setOrderTypeId(Integer orderTypeId) { this.orderTypeId = orderTypeId; }
    public BigDecimal getStartPrice() { return startPrice; }
    public void setStartPrice(BigDecimal startPrice) { this.startPrice = startPrice; }
    public BigDecimal getDeliveryPrice() { return deliveryPrice; }
    public void setDeliveryPrice(BigDecimal deliveryPrice) { this.deliveryPrice = deliveryPrice; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}