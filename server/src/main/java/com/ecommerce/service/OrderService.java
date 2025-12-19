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
import java.util.HashSet;
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
            
            // Flush để lấy orderId
            em.flush();
            
            // 6. Tạo notifications cho sellers
            Set<String> notifiedSellers = new HashSet<>();
            for (OrderDetail detail : order.getOrderDetails()) {
                String sellerId = detail.getProduct().getSeller().getUserId();
                
                // Chỉ gửi 1 notification cho mỗi seller (tránh duplicate nếu seller có nhiều sản phẩm trong đơn)
                if (!notifiedSellers.contains(sellerId)) {
                    notificationService.createNotification(
                        em,
                        sellerId,
                        NotificationType.NEW_ORDER,
                        "Đơn hàng mới #" + order.getOrderId(),
                        "Bạn có đơn hàng mới từ " + buyer.getFullName() + ". Vui lòng xác nhận đơn hàng.",
                        order.getOrderId()
                    );
                    notifiedSellers.add(sellerId);
                }
            }
            
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
        System.out.println("=== [OrderService] getOrdersByBuyer called with buyerId: " + buyerId);
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Use JOIN FETCH to eagerly load payment and orderDetails
            TypedQuery<Order> query = em.createQuery(
                "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.payment " +
                "LEFT JOIN FETCH o.orderDetails od " +
                "LEFT JOIN FETCH od.product p " +
                "LEFT JOIN FETCH p.seller " +
                "WHERE o.buyer.userId = :buyerId " +
                "ORDER BY o.orderDate DESC", 
                Order.class
            );
            query.setParameter("buyerId", buyerId);
            List<Order> results = query.getResultList();
            System.out.println("=== [OrderService] Query returned " + results.size() + " orders");
            return results;
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
            
            // Flush để đảm bảo order được update
            em.flush();
            
            // Tạo notification cho buyer khi trạng thái thay đổi
            String buyerId = order.getBuyer().getUserId();
            NotificationType notificationType = null;
            String title = null;
            String message = null;
            
            switch (newStatus) {
                case CONFIRMED:
                    notificationType = NotificationType.ORDER_CONFIRMED;
                    title = "Đơn hàng đã được xác nhận";
                    message = "Đơn hàng #" + orderId + " đã được xác nhận và đang được chuẩn bị.";
                    break;
                case SHIPPING:
                    notificationType = NotificationType.ORDER_SHIPPING;
                    title = "Đơn hàng đang được giao";
                    message = "Đơn hàng #" + orderId + " đang trên đường giao đến bạn.";
                    
                    // Gửi notification cho shipper nếu có
                    if (order.getShipper() != null) {
                        notificationService.createNotification(
                            em,
                            order.getShipper().getUserId(),
                            NotificationType.NEW_DELIVERY,
                            "Đơn hàng mới cần giao #" + orderId,
                            "Bạn có đơn hàng mới cần giao đến " + order.getShippingAddress(),
                            orderId
                        );
                    }
                    break;
                case DELIVERED:
                    notificationType = NotificationType.ORDER_DELIVERED;
                    title = "Đơn hàng đã được giao";
                    message = "Đơn hàng #" + orderId + " đã được giao thành công. Cảm ơn bạn đã mua hàng!";
                    break;
                case CANCELLED:
                    notificationType = NotificationType.ORDER_CANCELLED;
                    title = "Đơn hàng đã bị hủy";
                    message = "Đơn hàng #" + orderId + " đã bị hủy.";
                    break;
            }
            
            // Gửi notification cho buyer
            if (notificationType != null) {
                notificationService.createNotification(
                    em,
                    buyerId,
                    notificationType,
                    title,
                    message,
                    orderId
                );
            }
            
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
            
            // Flush để đảm bảo order được update
            em.flush();
            
            // Gửi notification cho shipper khi được gán
            System.out.println("=== [OrderService] Shipper assigned to order, creating notification");
            System.out.println("=== [OrderService] Shipper: " + shipper.getEmail() + ", Order: " + orderId);
            
            try {
                notificationService.createNotification(
                    em,
                    shipperId,
                    NotificationType.NEW_DELIVERY,
                    "Đơn hàng mới được gán #" + orderId,
                    "Bạn được gán giao đơn hàng #" + orderId + " đến " + order.getShippingAddress(),
                    orderId
                );
                System.out.println("=== [OrderService] ✅ Notification created successfully");
            } catch (Exception e) {
                System.err.println("=== [OrderService] ❌ Failed to create notification: " + e.getMessage());
                e.printStackTrace();
            }
            
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
