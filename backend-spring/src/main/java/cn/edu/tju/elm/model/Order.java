package cn.edu.tju.elm.model;

import cn.edu.tju.core.model.BaseEntity;
import cn.edu.tju.core.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal orderTotal;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private DeliveryAddress deliveryAddress;
    private Integer orderState;

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

}