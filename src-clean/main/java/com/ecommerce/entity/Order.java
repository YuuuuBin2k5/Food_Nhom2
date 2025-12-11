package com.ecommerce.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @Column(name = "order_id", nullable = false, length = 50)
    private String orderId;
    
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
    
    public Order() {
    }
    
    public void addOrderDetail(OrderDetail detail) {
        orderDetails.add(detail);
        detail.setOrder(this);
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setOrder(this);
        }
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }
    
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    public Payment getPayment() {
        return payment;
    }
}
