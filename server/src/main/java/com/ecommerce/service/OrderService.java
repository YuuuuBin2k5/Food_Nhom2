package com.ecommerce.service;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.*;
import com.ecommerce.util.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;

public class OrderService {

    // Tạo đơn hàng mới (Checkout)
    public List<String> placeOrder(CheckoutRequest request) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        List<String> createdOrderIds = new ArrayList<>();

        try {
            em.getTransaction().begin();

            // Tìm Buyer
            Buyer buyer = em.find(Buyer.class, request.getUserId());

            if (buyer == null) {
                throw new Exception("Buyer not found: " + request.getUserId());
            }

            List<Long> productIds = request.getItems().stream()
                    .map(item -> item.getProduct().getProductId())
                    .toList();

            Map<Long, Product> productMap = new HashMap<>();

            for (Long productId : productIds) {
                Product product = em.find(Product.class, productId, LockModeType.PESSIMISTIC_WRITE);
                if (product != null) {
                    product.getSeller().getUserId();
                    productMap.put(productId, product);
                }
            }

            // TÁCH GIỎ HÀNG THEO SELLER
            Map<String, List<CartItemDTO>> itemsBySeller = new HashMap<>();

            for (CartItemDTO itemDTO : request.getItems()) {
                Long productId = itemDTO.getProduct().getProductId();
                Product product = productMap.get(productId);

                if (product == null)
                    throw new Exception("Product not found: " + productId);
                if (product.getQuantity() < itemDTO.getQuantity()) {
                    throw new Exception("Sản phẩm '" + product.getName() + "' không đủ hàng!");
                }

                String sellerId = product.getSeller().getUserId();
                itemsBySeller.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(itemDTO);
            }

            // TẠO ĐƠN HÀNG RIÊNG CHO TỪNG SELLER
            for (Map.Entry<String, List<CartItemDTO>> entry : itemsBySeller.entrySet()) {
                List<CartItemDTO> sellerItems = entry.getValue();

                Order order = new Order();
                order.setBuyer(buyer);
                order.setShippingAddress(request.getShippingAddress());
                order.setOrderDetails(new ArrayList<>());

                double subTotal = 0.0;

                for (CartItemDTO itemDTO : sellerItems) {
                    Long productId = itemDTO.getProduct().getProductId();
                    Product product = productMap.get(productId);

                    product.setQuantity(product.getQuantity() - itemDTO.getQuantity());

                    OrderDetail detail = new OrderDetail();
                    detail.setProduct(product);
                    detail.setQuantity(itemDTO.getQuantity());
                    detail.setPriceAtPurchase(product.getSalePrice());
                    detail.setOrder(order);

                    order.getOrderDetails().add(detail);
                    subTotal += product.getSalePrice() * itemDTO.getQuantity();
                }

                double shippingFee = 30000;
                double finalAmount = subTotal + shippingFee;

                Payment payment = new Payment();
                payment.setAmount(finalAmount);
                payment.setMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
                payment.setPaymentDate(new Date());
                payment.setOrder(order);
                order.setPayment(payment);

                em.persist(order);
                em.flush();

                createdOrderIds.add(String.valueOf(order.getOrderId()));
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
                    Order.class);
            query.setParameter("buyerId", buyerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy đơn hàng theo seller
    public List<Order> getOrdersBySellerProducts(String sellerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Long> countQuery = em.createQuery(
                    "SELECT COUNT(p) FROM Product p WHERE p.seller.userId = :sellerId",
                    Long.class);
            countQuery.setParameter("sellerId", sellerId);
            Long productCount = countQuery.getSingleResult();

            if (productCount == 0) {
                return new ArrayList<>();
            }

            TypedQuery<Order> query = em.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                            "LEFT JOIN FETCH o.payment " +
                            "LEFT JOIN FETCH o.orderDetails od " +
                            "LEFT JOIN FETCH od.product p " +
                            "LEFT JOIN FETCH p.seller " +
                            "LEFT JOIN FETCH o.buyer " +
                            "WHERE p.seller.userId = :sellerId " +
                            "ORDER BY o.orderDate DESC",
                    Order.class);
            query.setParameter("sellerId", sellerId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in getOrdersBySellerProducts: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // Lấy đơn hàng theo shipper
    public List<Order> getOrdersByShipper(String shipperId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                            "LEFT JOIN FETCH o.payment " +
                            "LEFT JOIN FETCH o.orderDetails od " +
                            "LEFT JOIN FETCH od.product p " +
                            "LEFT JOIN FETCH p.seller " +
                            "LEFT JOIN FETCH o.buyer " +
                            "WHERE o.shipper.userId = :shipperId " +
                            "ORDER BY o.orderDate DESC",
                    Order.class);
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
                    Order.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Lấy đơn hàng cho shipper
    public List<Order> getOrdersForShipper(String shipperId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Order> query = em.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                            "LEFT JOIN FETCH o.payment " +
                            "LEFT JOIN FETCH o.orderDetails od " +
                            "LEFT JOIN FETCH od.product p " +
                            "LEFT JOIN FETCH p.seller " +
                            "LEFT JOIN FETCH o.buyer " +
                            "LEFT JOIN FETCH o.shipper " +
                            "WHERE o.status = :confirmed " +
                            "OR (o.shipper.userId = :shipperId AND (o.status = :shipping OR o.status = :delivered)) " +
                            "ORDER BY o.orderDate DESC",
                    Order.class);
            query.setParameter("confirmed", OrderStatus.CONFIRMED);
            query.setParameter("shipping", OrderStatus.SHIPPING);
            query.setParameter("delivered", OrderStatus.DELIVERED);
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

    // Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null)
                throw new Exception("Order not found: " + orderId);

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

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Tính tổng doanh thu của seller
    public double calculateSellerRevenue(String sellerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT COALESCE(SUM(o.payment.amount), 0.0) FROM Order o " +
                            "JOIN o.orderDetails od " +
                            "JOIN od.product p " +
                            "WHERE p.seller.userId = :sellerId AND o.status = :status",
                    Double.class);
            query.setParameter("sellerId", sellerId);
            query.setParameter("status", OrderStatus.DELIVERED);

            Double result = query.getSingleResult();
            return result != null ? result : 0.0;
        } catch (Exception e) {
            System.err.println("Error calculating seller revenue: " + e.getMessage());
            return 0.0;
        } finally {
            em.close();
        }
    }

    // Cập nhật trạng thái đơn hàng với shipper ID
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String shipperId) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null)
                throw new Exception("Order not found: " + orderId);

            // Set shipper khi accept order
            if (newStatus == OrderStatus.SHIPPING && order.getStatus() == OrderStatus.CONFIRMED) {
                Shipper shipper = em.find(Shipper.class, shipperId);
                if (shipper == null)
                    throw new Exception("Shipper not found: " + shipperId);
                order.setShipper(shipper);
            }

            order.setStatus(newStatus);
            em.merge(order);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
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
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    
    // Kiểm tra shipper có đơn đang giao (SHIPPING) không
    private boolean hasActiveDelivery(EntityManager em, String shipperId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.shipper.userId = :shipperId AND o.status = :status",
            Long.class
        );
        query.setParameter("shipperId", shipperId);
        query.setParameter("status", OrderStatus.SHIPPING);
        return query.getSingleResult() > 0;
    }
    
    // Public method để check từ servlet/JSP
    public boolean shipperHasActiveDelivery(String shipperId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return hasActiveDelivery(em, shipperId);
        } finally {
            em.close();
        }
    }
}