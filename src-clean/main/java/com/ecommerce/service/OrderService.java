package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.*;
import com.ecommerce.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import java.util.Date;

public class OrderService {
    
    public String placeOrder(CheckoutRequest request) throws Exception {
        
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            String orderId = generateOrderId();
            
            Order order = new Order();
            order.setOrderId(orderId);
            order.setUserId(request.getUserId());
            order.setShippingAddress(request.getShippingAddress());
            order.setStatus(OrderStatus.PENDING);
            order.setCreatedAt(new Date());
            
            double totalAmount = 0.0;
            
            for (CartItemDTO item : request.getItems()) {
                Product product = em.find(Product.class, item.getProductId(), LockModeType.PESSIMISTIC_WRITE);
                
                if (product == null) {
                    throw new Exception("Product not found: " + item.getProductId());
                }
                
                if (!product.hasEnoughStock(item.getQuantity())) {
                    throw new Exception("Not enough stock for product: " + product.getName());
                }
                
                product.deductStock(item.getQuantity());
                em.merge(product);
                
                OrderDetail detail = new OrderDetail();
                detail.setProduct(product);
                detail.setQuantity(item.getQuantity());
                detail.setPriceAtPurchase(product.getSalePrice());
                
                order.addOrderDetail(detail);
                
                totalAmount += product.getSalePrice() * item.getQuantity();
            }
            
            order.setTotalAmount(totalAmount);
            
            Payment payment = new Payment();
            payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
            payment.setAmount(totalAmount);
            payment.setPaymentDate(new Date());
            
            order.setPayment(payment);
            
            em.persist(order);
            
            transaction.commit();
            
            return orderId;
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis();
    }
}
