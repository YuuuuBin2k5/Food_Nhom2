package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.*;

public class OrderService {

    private final NotificationService notificationService = new NotificationService();
    public List<String> placeOrder(CheckoutRequest request) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        List<String> createdOrderIds = new ArrayList<>();

        try {
            em.getTransaction().begin();

            // 1. Tìm Buyer
            Buyer buyer = em.find(Buyer.class, request.getUserId());
            if (buyer == null) {
                throw new Exception("Buyer not found: " + request.getUserId());
            }

            // 2. TÁCH GIỎ HÀNG THEO SELLER
            // Map<SellerId, List<Items>>
            Map<String, List<CartItemDTO>> itemsBySeller = new HashMap<>();

            for (CartItemDTO itemDTO : request.getItems()) {
                Long productId = Long.parseLong(itemDTO.getProductId());
                Product product = em.find(Product.class, productId);

                if (product == null) throw new Exception("Product not found: " + productId);
                if (product.getQuantity() < itemDTO.getQuantity()) {
                    throw new Exception("Sản phẩm '" + product.getName() + "' không đủ hàng!");
                }

                String sellerId = product.getSeller().getUserId();
                itemsBySeller.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(itemDTO);
            }

            // 3. TẠO ĐƠN HÀNG RIÊNG CHO TỪNG SELLER
            for (Map.Entry<String, List<CartItemDTO>> entry : itemsBySeller.entrySet()) {
                String sellerId = entry.getKey();
                List<CartItemDTO> sellerItems = entry.getValue();

                Order order = new Order();
                order.setBuyer(buyer);
                order.setShippingAddress(request.getShippingAddress());
                // Status và Date đã có trong constructor Order()
                order.setOrderDetails(new ArrayList<>());

                double subTotal = 0.0;

                for (CartItemDTO itemDTO : sellerItems) {
                    Long productId = Long.parseLong(itemDTO.getProductId());
                    Product product = em.find(Product.class, productId);

                    // Trừ tồn kho
                    product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
                    em.merge(product);

                    // Tạo OrderDetail
                    OrderDetail detail = new OrderDetail();
                    detail.setProduct(product);
                    detail.setQuantity(itemDTO.getQuantity());
                    detail.setPriceAtPurchase(product.getSalePrice());
                    detail.setOrder(order); // Link ngược lại Order

                    order.getOrderDetails().add(detail);
                    subTotal += product.getSalePrice() * itemDTO.getQuantity();
                }

                // Tính Payment cho đơn hàng con này
                // Giả sử phí ship 30k cho mỗi đơn (hoặc bạn có thể chia nhỏ logic này)
                double shippingFee = 30000;
                double finalAmount = subTotal + shippingFee;

                Payment payment = new Payment();
                payment.setAmount(finalAmount);
                payment.setMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
                payment.setPaymentDate(new Date());
                payment.setOrder(order);
                order.setPayment(payment);

                // Lưu đơn hàng
                em.persist(order);
                em.flush(); // Để lấy ID ngay lập tức

                createdOrderIds.add(String.valueOf(order.getOrderId()));

                // Gửi thông báo cho Seller sở hữu đơn này
                try {
                    notificationService.createNotification(
                            em,
                            sellerId,
                            NotificationType.NEW_ORDER,
                            "Đơn hàng mới #" + order.getOrderId(),
                            "Bạn có đơn hàng mới trị giá " + finalAmount,
                            order.getOrderId()
                    );
                } catch (Exception ex) {
                    System.err.println("Lỗi gửi thông báo: " + ex.getMessage());
                }
            }

            em.getTransaction().commit();
            return createdOrderIds;

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

    // Cập nhật trạng thái đơn hàng (bao gồm logic hoàn kho khi HỦY)
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null) throw new Exception("Order not found: " + orderId);

            // Logic hoàn kho nếu HỦY đơn
            if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    Product p = detail.getProduct();
                    p.setQuantity(p.getQuantity() + detail.getQuantity());
                    em.merge(p);
                }
            }

            order.setStatus(newStatus);
            em.merge(order);

            // Notification logic (Giữ nguyên như cũ)...
            // (Bạn có thể copy lại phần switch case notification từ file cũ vào đây nếu cần)

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Gán shipper
    public void assignShipper(Long orderId, String shipperId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, orderId);
            Shipper shipper = em.find(Shipper.class, shipperId);
            if (order != null && shipper != null) {
                order.setShipper(shipper);
                em.merge(order);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}