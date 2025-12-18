package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

public class OrderService {

    // 1. Đặt hàng (ĐÃ SỬA: Tách đơn hàng theo Seller)
    public List<String> placeOrder(CheckoutRequest request) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        List<String> orderIds = new ArrayList<>();

        try {
            em.getTransaction().begin();

            Buyer buyer = em.find(Buyer.class, request.getUserId());
            if (buyer == null) throw new Exception("Buyer not found");

            // Nhóm các item theo Seller ID
            Map<String, List<CartItemDTO>> itemsBySeller = new HashMap<>();

            for (CartItemDTO itemDTO : request.getItems()) {
                Product p = em.find(Product.class, Long.parseLong(itemDTO.getProductId()));
                if (p == null) throw new Exception("Sản phẩm không tồn tại: " + itemDTO.getProductId());

                // Validate tồn kho
                if (p.getQuantity() < itemDTO.getQuantity()) {
                    throw new Exception("Sản phẩm '" + p.getName() + "' không đủ số lượng tồn kho!");
                }

                String sellerId = p.getSeller().getUserId();
                itemsBySeller.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(itemDTO);
            }

            // Tạo Order riêng cho từng Seller
            for (Map.Entry<String, List<CartItemDTO>> entry : itemsBySeller.entrySet()) {
                List<CartItemDTO> sellerItems = entry.getValue();

                Order order = new Order();
                order.setBuyer(buyer);
                order.setShippingAddress(request.getShippingAddress());
                order.setStatus(OrderStatus.PENDING);
                order.setOrderDate(new Date());
                order.setOrderDetails(new ArrayList<>());

                double subTotal = 0;

                for (CartItemDTO itemDTO : sellerItems) {
                    Product product = em.find(Product.class, Long.parseLong(itemDTO.getProductId()));

                    // TRỪ TỒN KHO
                    product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
                    em.merge(product);

                    OrderDetail detail = new OrderDetail();
                    detail.setOrder(order);
                    detail.setProduct(product);
                    detail.setQuantity(itemDTO.getQuantity());
                    detail.setPriceAtPurchase(product.getSalePrice());

                    order.getOrderDetails().add(detail);
                    subTotal += product.getSalePrice() * itemDTO.getQuantity();
                }

                // Phí ship (Giả định mỗi đơn 30k)
                double shippingFee = 30000;
                double total = subTotal + shippingFee;

                Payment payment = new Payment();
                payment.setAmount(total);
                payment.setMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
                payment.setPaymentDate(new Date());
                payment.setOrder(order);
                order.setPayment(payment);

                em.persist(order);
                em.flush(); // Để lấy được ID ngay
                orderIds.add(order.getOrderId().toString());
            }

            em.getTransaction().commit();
            return orderIds;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 2. Hủy đơn hàng (Hoàn lại tồn kho)
    public void cancelOrder(Long orderId, String userId, String role) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new Exception("Đơn hàng không tồn tại");

            // Check quyền
            if ("BUYER".equals(role) && !order.getBuyer().getUserId().equals(userId)) {
                throw new Exception("Bạn không có quyền hủy đơn này");
            }
            // Seller chỉ được hủy đơn có chứa sản phẩm của mình (Logic tách đơn đã đảm bảo 1 order = 1 seller)
            if ("SELLER".equals(role)) {
                boolean isOwner = order.getOrderDetails().stream()
                        .anyMatch(od -> od.getProduct().getSeller().getUserId().equals(userId));
                if (!isOwner) throw new Exception("Đơn hàng này không thuộc về Shop của bạn");
            }

            // Chỉ hủy khi chưa giao
            if (order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.DELIVERED) {
                throw new Exception("Không thể hủy đơn đang giao hoặc đã giao");
            }

            if (order.getStatus() == OrderStatus.CANCELLED) {
                return; // Đã hủy rồi thì thôi
            }

            order.setStatus(OrderStatus.CANCELLED);

            // LOGIC HOÀN KHO
            for (OrderDetail od : order.getOrderDetails()) {
                Product p = od.getProduct();
                p.setQuantity(p.getQuantity() + od.getQuantity());
                em.merge(p);
            }

            em.merge(order);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 3. Update Status (Dành cho Seller duyệt/giao hàng)
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String sellerId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, orderId);
            if (order == null) throw new Exception("Order not found");

            // Verify Seller Ownership
            boolean isOwner = order.getOrderDetails().stream()
                    .anyMatch(od -> od.getProduct().getSeller().getUserId().equals(sellerId));
            if (!isOwner) throw new Exception("Unauthorized Seller");

            // Nếu Seller chọn CANCELLED -> Gọi hàm cancelOrder để hoàn kho
            if (newStatus == OrderStatus.CANCELLED) {
                // Rollback transaction này để gọi hàm cancelOrder trọn vẹn
                em.getTransaction().rollback();
                em.close();
                cancelOrder(orderId, sellerId, "SELLER");
                return;
            }

            order.setStatus(newStatus);
            em.merge(order);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    // 4. Get Orders By Buyer (Fix Lazy Loading)
    public List<Order> getOrdersByBuyer(String buyerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT o FROM Order o " +
                                    "LEFT JOIN FETCH o.orderDetails od " +
                                    "LEFT JOIN FETCH od.product p " +
                                    "LEFT JOIN FETCH p.seller " +
                                    "WHERE o.buyer.userId = :uid ORDER BY o.orderDate DESC", Order.class)
                    .setParameter("uid", buyerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 5. Get Orders By Seller (Cho trang quản lý Seller)
    public List<Order> getOrdersBySeller(String sellerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Chỉ lấy những Order mà sản phẩm bên trong thuộc về Seller này
            return em.createQuery(
                            "SELECT DISTINCT o FROM Order o " +
                                    "JOIN FETCH o.orderDetails od " +
                                    "JOIN FETCH od.product p " +
                                    "JOIN FETCH o.buyer " +
                                    "WHERE p.seller.userId = :sid ORDER BY o.orderDate DESC", Order.class)
                    .setParameter("sid", sellerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Order getOrderById(Long id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }
}