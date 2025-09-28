package cn.edu.tju.elm.model.dto;

import cn.edu.tju.elm.model.Business;
import java.math.BigDecimal;

/**
 * 商家数据传输对象 (Data Transfer Object)
 * 用于向前端返回数据，包含了商家基本信息和动态计算出的距离等字段。
 */
public class BusinessDTO {

    private Long id;
    private String businessName;
    private String businessAddress;
    private String businessExplain;
    private String businessImg;
    private Integer orderTypeId;
    private BigDecimal startPrice;
    private BigDecimal deliveryPrice;

    // 添加前端首页需要的字段 (假设您的 Business 实体中也存在这些字段)
    private Double rating;
    private Integer monthlySales;

    // 这是动态计算出的距离字段
    private Double distance;

    // 无参构造函数，保留它是一个好习惯
    public BusinessDTO() {}

    /**
     * 核心构造函数，用于将数据库实体 Business 转换为 DTO
     * @param business 从数据库查询出的 Business 对象
     */
    public BusinessDTO(Business business) {
        this.id = business.getId();
        this.businessName = business.getBusinessName();
        this.businessAddress = business.getBusinessAddress();
        this.businessExplain = business.getBusinessExplain();
        this.businessImg = business.getBusinessImg();
        this.orderTypeId = business.getOrderTypeId();
        this.startPrice = business.getStartPrice();
        this.deliveryPrice = business.getDeliveryPrice();

        // 假设 Business 实体中有 getRating() 和 getMonthlySales() 方法
        // 如果您的实体类中没有这两个字段，请根据实际情况删除或注释掉下面两行
        // this.rating = business.getRating();
         this.monthlySales = business.getMonthlySales();
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getMonthlySales() {
        return monthlySales;
    }

    public void setMonthlySales(Integer monthlySales) {
        this.monthlySales = monthlySales;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}