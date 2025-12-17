package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderService {
    
    // Tạo đơn hàng mới (Checkout)
    public String placeOrder(CheckoutRequest request) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            // 1. Tìm Buyer
            Buyer buyer = em.find(Buyer.class, request.getUserId());
            if (buyer == null) {
                throw new Exception("Buyer not found: " + request.getUserId());
            }
            
            // 2. Tạo Order mới
            Order order = new Order();
            // orderDate và status đã được set trong constructor
            order.setShippingAddress(request.getShippingAddress());
            order.setBuyer(buyer);
            order.setOrderDetails(new ArrayList<>());
            
            double totalAmount = 0.0;
            
            // 3. Xử lý từng item trong giỏ hàng
            for (CartItemDTO item : request.getItems()) {
                // Convert String productId to Long
                Long productId = Long.parseLong(item.getProductId());
                Product product = em.find(Product.class, productId);
                if (product == null) {
                    throw new Exception("Product not found: " + item.getProductId());
                }
                
                // Kiểm tra tồn kho
                if (product.getQuantity() < item.getQuantity()) {
                    throw new Exception("Not enough stock for product: " + product.getName());
                }
                
                // Trừ tồn kho
                product.setQuantity(product.getQuantity() - item.getQuantity());
                em.merge(product);
                
                // Tạo OrderDetail
                OrderDetail detail = new OrderDetail();
                detail.setProduct(product);
                detail.setQuantity(item.getQuantity());
                detail.setPriceAtPurchase(product.getSalePrice());
                detail.setOrder(order);
                
                order.getOrderDetails().add(detail);
                
                totalAmount += product.getSalePrice() * item.getQuantity();
            }
            
            // 4. Tạo Payment
            Payment payment = new Payment();
            payment.setAmount(totalAmount);
            payment.setMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            payment.setPaymentDate(new Date());
            
            order.setPayment(payment);
            
            // 5. Lưu Order (cascade sẽ tự động lưu OrderDetail và Payment)
            em.persist(order);
            
            em.getTransaction().commit();
            
            return order.getOrderId().toString();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Lấy tất cả đơn hàng
    public List<Order> getAllOrders() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy đơn hàng theo buyer
    public List<Order> getOrdersByBuyer(String buyerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.buyer.userId = :buyerId ORDER BY o.orderDate DESC", 
                Order.class
            );
            query.setParameter("buyerId", buyerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy đơn hàng theo shipper
    public List<Order> getOrdersByShipper(String shipperId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.shipper.userId = :shipperId ORDER BY o.orderDate DESC", 
                Order.class
            );
            query.setParameter("shipperId", shipperId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy đơn hàng theo status
    public List<Order> getOrdersByStatus(OrderStatus status) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.orderDate DESC", 
                Order.class
            );
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy đơn hàng theo ID
    public Order getOrderById(Long orderId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Order order = em.find(Order.class, orderId);
            if (order == null) {
                throw new Exception("Order not found: " + orderId);
            }
            return order;
        } finally {
            em.close();
        }
    }

    // Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            
            Order order = em.find(Order.class, orderId);
            if (order == null) {
                throw new Exception("Order not found: " + orderId);
            }
            
            order.setStatus(newStatus);
            em.merge(order);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Gán shipper cho đơn hàng
    public void assignShipper(Long orderId, String shipperId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            
            Order order = em.find(Order.class, orderId);
            if (order == null) {
                throw new Exception("Order not found: " + orderId);
            }
            
            Shipper shipper = em.find(Shipper.class, shipperId);
            if (shipper == null) {
                throw new Exception("Shipper not found: " + shipperId);
            }
            
            order.setShipper(shipper);
            em.merge(order);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
