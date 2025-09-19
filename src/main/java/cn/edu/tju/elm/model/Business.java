package cn.edu.tju.elm.model;

import cn.edu.tju.core.model.BaseEntity;
import cn.edu.tju.core.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "business")
public class Business extends BaseEntity {

    @Column(name = "business_name")
    private String businessName;

    // 已修正: 将 FetchType 从 LAZY 改为 EAGER
    // 这可以确保在序列化为 JSON 时，businessOwner 的数据已经被加载，从而避免 500 错误。
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

    // Getters and Setters
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public User getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(User businessOwner) {
        this.businessOwner = businessOwner;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessExplain() {
        return businessExplain;
    }

    public void setBusinessExplain(String businessExplain) {
        this.businessExplain = businessExplain;
    }

    public String getBusinessImg() {
        return businessImg;
    }

    public void setBusinessImg(String businessImg) {
        this.businessImg = businessImg;
    }

    public Integer getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(Integer orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}