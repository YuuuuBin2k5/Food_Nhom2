package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderService {
    
    private final NotificationService notificationService = new NotificationService();
    
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
            
            // 6. Thu thập thông tin seller TRƯỚC KHI commit (tránh LazyInitializationException)
            Set<String> sellerIds = new HashSet<>();
            for (OrderDetail detail : order.getOrderDetails()) {
                String sellerId = detail.getProduct().getSeller().getUserId();
                sellerIds.add(sellerId);
            }
            String buyerName = buyer.getFullName();
            Long orderId = order.getOrderId();
            
            em.getTransaction().commit();
            
            // 7. Gửi thông báo cho các seller (SAU KHI commit thành công)
            for (String sellerId : sellerIds) {
                try {
                    // Create new EntityManager for notification (after main transaction committed)
                    EntityManager notifEm = DBUtil.getEmFactory().createEntityManager();
                    try {
                        notifEm.getTransaction().begin();
                        notificationService.createNotification(
                            notifEm,
                            sellerId,
                            NotificationType.NEW_ORDER,
                            "Đơn hàng mới #" + orderId,
                            "Bạn có đơn hàng mới từ " + buyerName,
                            orderId
                        );
                        notifEm.getTransaction().commit();
                    } catch (Exception e) {
                        if (notifEm.getTransaction().isActive()) {
                            notifEm.getTransaction().rollback();
                        }
                        throw e;
                    } finally {
                        notifEm.close();
                    }
                } catch (Exception e) {
                    // Log error nhưng không throw để không ảnh hưởng đến order
                    System.err.println("Failed to send notification to seller " + sellerId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return orderId.toString();
            
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
            
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(newStatus);
            em.merge(order);
            
            em.getTransaction().commit();
            
            // Gửi thông báo cho buyer khi trạng thái thay đổi
            String buyerId = order.getBuyer().getUserId();
            String statusMessage = getStatusMessage(newStatus);
            NotificationType notificationType = getNotificationTypeForStatus(newStatus);
            
            if (notificationType != null) {
                notificationService.createNotification(
                    buyerId,
                    notificationType,
                    "Cập nhật đơn hàng #" + orderId,
                    statusMessage,
                    orderId
                );
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    // Helper method để lấy message theo status
    private String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Đơn hàng của bạn đã được xác nhận";
            case SHIPPING -> "Đơn hàng của bạn đang được giao";
            case DELIVERED -> "Đơn hàng của bạn đã được giao thành công";
            case CANCELLED -> "Đơn hàng của bạn đã bị hủy";
            default -> "Trạng thái đơn hàng đã được cập nhật";
        };
    }
    
    // Helper method để lấy notification type theo status
    private NotificationType getNotificationTypeForStatus(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> NotificationType.ORDER_CONFIRMED;
            case SHIPPING -> NotificationType.ORDER_SHIPPING;
            case DELIVERED -> NotificationType.ORDER_DELIVERED;
            case CANCELLED -> NotificationType.ORDER_CANCELLED;
            default -> null;
        };
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
            
            // Gửi thông báo cho shipper
            notificationService.createNotification(
                shipper.getUserId(),
                NotificationType.NEW_DELIVERY,
                "Đơn hàng mới cần giao #" + orderId,
                "Bạn được giao nhiệm vụ giao đơn hàng mới",
                orderId
            );
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
