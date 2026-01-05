package com.ecommerce.servlet;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.service.ProductService;
import com.ecommerce.util.MenuHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Handles shopping cart page display and form-based cart operations
 * Session-based cart management (no database persistence until checkout)
 */
@WebServlet(name = "CartServlet", urlPatterns = { "/cart", "/view-cart" })
public class CartServlet extends HttpServlet {

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authentication check
        HttpSession session = request.getSession(false);
        
        // ✅ DEBUG: Log session info
        System.out.println("=== [CartServlet] ===");
        if (session != null) {
            System.out.println("Session ID: " + session.getId());
            System.out.println("User: " + session.getAttribute("user"));
        } else {
            System.out.println("❌ No session found");
        }
        
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("❌ User not authenticated, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Initialize cart if not exists
        List<CartItemDTO> cart = getOrCreateCart(session);
        
        // ✅ DEBUG: Log cart info
        System.out.println("Cart size: " + cart.size());
        if (!cart.isEmpty()) {
            System.out.println("Cart items:");
            for (CartItemDTO item : cart) {
                System.out.println("  - " + item.getProduct().getName() + " x" + item.getQuantity());
            }
        }

        // Calculate cart summary for display
        CartSummary summary = calculateCartSummary(cart);

        // Set attributes for JSP
        request.setAttribute("cartItems", cart);
        request.setAttribute("subtotal", summary.subtotal);
        request.setAttribute("totalSavings", summary.savings);
        request.setAttribute("total", summary.total);
        request.setAttribute("itemCount", summary.itemCount);

        // Set menu items
        MenuHelper.setMenuItems(request, "BUYER", "/cart");

        // Forward to cart.jsp
        System.out.println("✅ Forwarding to cart.jsp");
        request.getRequestDispatcher("/buyer/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authentication check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null)
            action = "";

        List<CartItemDTO> cart = getOrCreateCart(session);

        try {
            switch (action) {
                case "add":
                    handleAdd(request, cart);
                    session.setAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng!");
                    break;

                case "update":
                    handleUpdate(request, cart);
                    session.setAttribute("successMessage", "Đã cập nhật giỏ hàng!");
                    break;

                case "remove":
                    handleRemove(request, cart);
                    session.setAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
                    break;

                case "clear":
                    cart.clear();
                    session.setAttribute("successMessage", "Đã xóa toàn bộ giỏ hàng!");
                    break;

                default:
                    session.setAttribute("errorMessage", "Hành động không hợp lệ!");
            }

            // Update cart in session
            session.setAttribute("cart", cart);
            updateCartSize(session, cart);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ!");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        // Redirect based on action
        String referer = request.getHeader("Referer");
        if ("add".equals(action) && referer != null && !referer.contains("cart")) {
            // If added from product page, stay there
            response.sendRedirect(referer);
        } else {
            // Otherwise go to cart page
            response.sendRedirect(request.getContextPath() + "/view-cart");
        }
    }

    /**
     * Add product to cart (or increment quantity if exists)
     */
    private void handleAdd(HttpServletRequest request, List<CartItemDTO> cart) throws Exception {
        Long productId = Long.parseLong(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Check if item already in cart
        boolean found = false;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        // If not found, add new item
        if (!found) {
            ProductDTO product = productService.getProductById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }

            // Check stock availability
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("Không đủ hàng trong kho (Còn: " + product.getQuantity() + ")");
            }

            cart.add(new CartItemDTO(product, quantity));
        }
    }

    /**
     * Update cart item quantity
     */
    private void handleUpdate(HttpServletRequest request, List<CartItemDTO> cart) {
        Long productId = Long.parseLong(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        if (quantity <= 0) {
            handleRemove(request, cart);
            return;
        }

        for (CartItemDTO item : cart) {
            if (item.getProduct().getProductId().equals(productId)) {
                // Check stock availability
                if (item.getProduct().getQuantity() < quantity) {
                    throw new IllegalArgumentException("Không đủ hàng (Còn: " + item.getProduct().getQuantity() + ")");
                }
                item.setQuantity(quantity);
                break;
            }
        }
    }

    /**
     * Remove item from cart
     */
    private void handleRemove(HttpServletRequest request, List<CartItemDTO> cart) {
        Long productId = Long.parseLong(request.getParameter("productId"));

        Iterator<CartItemDTO> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartItemDTO item = iterator.next();
            if (item.getProduct().getProductId().equals(productId)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Get cart from session or create new one
     */
    @SuppressWarnings("unchecked")
    private List<CartItemDTO> getOrCreateCart(HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    /**
     * Update cart size in session (for header display)
     */
    private void updateCartSize(HttpSession session, List<CartItemDTO> cart) {
        int totalItems = cart.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
        session.setAttribute("cartSize", totalItems);
    }

    /**
     * Calculate cart summary (subtotal, savings, total)
     */
    private CartSummary calculateCartSummary(List<CartItemDTO> cart) {
        CartSummary summary = new CartSummary();

        for (CartItemDTO item : cart) {
            summary.subtotal += item.getSubtotal();
            summary.savings += item.getSavings();
            summary.itemCount += item.getQuantity();
        }

        summary.total = summary.subtotal;

        return summary;
    }

    /**
     * Inner class for cart summary data
     */
    private static class CartSummary {
        double subtotal = 0;
        double savings = 0;
        double total = 0;
        int itemCount = 0;
    }
}