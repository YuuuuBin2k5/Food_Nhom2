package com.ecommerce.servlet;

import com.ecommerce.dto.CartItemDTO;
import com.ecommerce.service.CartService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cart API Servlet
 * Handles cart operations: validate, add, update, remove items
 * Endpoints:
 *   POST   /api/cart/validate - Validate cart items
 *   POST   /api/cart/add - Add item to cart
 *   POST   /api/cart/update - Update item quantity
 *   DELETE /api/cart/remove - Remove item from cart
 *   GET    /api/cart/total - Calculate cart total
 */
@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService();
    private final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleInvalidRequest(response, out, "Invalid request");
                return;
            }

            if (pathInfo.equals("/total")) {
                handleGetTotal(request, response, out);
            } else {
                handleInvalidRequest(response, out, "Invalid endpoint");
            }
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleInvalidRequest(response, out, "Invalid request");
                return;
            }

            switch (pathInfo) {
                case "/validate":
                    handleValidateCart(request, response, out);
                    break;
                case "/add":
                    handleAddToCart(request, response, out);
                    break;
                case "/update":
                    handleUpdateQuantity(request, response, out);
                    break;
                default:
                    handleInvalidRequest(response, out, "Invalid endpoint");
            }
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleInvalidRequest(response, out, "Invalid request");
                return;
            }

            if (pathInfo.equals("/remove")) {
                handleRemoveFromCart(request, response, out);
            } else {
                handleInvalidRequest(response, out, "Invalid endpoint");
            }
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    /**
     * POST /api/cart/add
     * Add item to cart
     * Body: { productId: number, quantity: number }
     */
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            String body = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
            
            JsonObject requestBody = gson.fromJson(body, JsonObject.class);
            Long productId = requestBody.get("productId").getAsLong();
            int quantity = requestBody.get("quantity").getAsInt();

            // Get cart from session or create new one
            List<CartItemDTO> cart = getCartFromSession(request);
            
            // Add item
            cartService.addToCart(cart, productId, quantity);
            saveCartToSession(request, cart);

            // Return response
            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("message", "Đã thêm vào giỏ hàng");
            result.add("cart", gson.toJsonTree(cart));

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(result));
        } catch (IllegalArgumentException e) {
            handleError(response, out, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    /**
     * POST /api/cart/update
     * Update item quantity in cart
     * Body: { productId: number, quantity: number }
     */
    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            String body = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
            
            JsonObject requestBody = gson.fromJson(body, JsonObject.class);
            Long productId = requestBody.get("productId").getAsLong();
            int newQuantity = requestBody.get("quantity").getAsInt();

            // Get cart from session
            List<CartItemDTO> cart = getCartFromSession(request);
            
            // Update quantity
            cartService.updateQuantity(cart, productId, newQuantity);
            saveCartToSession(request, cart);

            // Return response
            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("message", "Đã cập nhật giỏ hàng");
            result.add("cart", gson.toJsonTree(cart));

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(result));
        } catch (IllegalArgumentException e) {
            handleError(response, out, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    /**
     * DELETE /api/cart/remove
     * Remove item from cart
     * Query param: productId
     */
    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            String productIdStr = request.getParameter("productId");
            if (productIdStr == null || productIdStr.isEmpty()) {
                throw new IllegalArgumentException("Missing productId parameter");
            }

            Long productId = Long.parseLong(productIdStr);

            // Get cart from session
            List<CartItemDTO> cart = getCartFromSession(request);
            
            // Remove item
            cartService.removeFromCart(cart, productId);
            saveCartToSession(request, cart);

            // Return response
            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("message", "Đã xóa khỏi giỏ hàng");
            result.add("cart", gson.toJsonTree(cart));

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(result));
        } catch (IllegalArgumentException e) {
            handleError(response, out, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    /**
     * POST /api/cart/validate
     * Validate cart items (check stock, existence)
     * Body: { items: CartItemDTO[] }
     */
    private void handleValidateCart(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            String body = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
            
            JsonObject requestBody = gson.fromJson(body, JsonObject.class);
            JsonArray itemsArray = requestBody.getAsJsonArray("items");
            
            List<CartItemDTO> cartItems = new ArrayList<>();
            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject item = itemsArray.get(i).getAsJsonObject();
                cartItems.add(new CartItemDTO(
                    item.get("productId").getAsString(),
                    item.get("quantity").getAsInt()
                ));
            }

            // Validate cart
            List<String> errors = cartService.validateCart(cartItems);

            // Return response
            JsonObject result = new JsonObject();
            result.addProperty("valid", errors.isEmpty());
            result.add("errors", gson.toJsonTree(errors));

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(result));
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    /**
     * GET /api/cart/total
     * Calculate cart total price
     * Query param: items (JSON array)
     */
    private void handleGetTotal(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            String itemsParam = request.getParameter("items");
            
            List<CartItemDTO> cartItems = new ArrayList<>();
            if (itemsParam != null && !itemsParam.isEmpty()) {
                JsonArray itemsArray = gson.fromJson(itemsParam, JsonArray.class);
                for (int i = 0; i < itemsArray.size(); i++) {
                    JsonObject item = itemsArray.get(i).getAsJsonObject();
                    cartItems.add(new CartItemDTO(
                        item.get("productId").getAsString(),
                        item.get("quantity").getAsInt()
                    ));
                }
            }

            // Calculate total
            double total = cartService.calculateTotal(cartItems);

            // Return response
            JsonObject result = new JsonObject();
            result.addProperty("total", total);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(result));
        } catch (Exception e) {
            handleError(response, out, e);
        }
    }

    /**
     * Get cart from session
     */
    @SuppressWarnings("unchecked")
    private List<CartItemDTO> getCartFromSession(HttpServletRequest request) {
        Object cartObj = request.getSession().getAttribute("cart");
        if (cartObj instanceof List) {
            return (List<CartItemDTO>) cartObj;
        }
        return new ArrayList<>();
    }

    /**
     * Save cart to session
     */
    private void saveCartToSession(HttpServletRequest request, List<CartItemDTO> cart) {
        request.getSession().setAttribute("cart", cart);
    }

    private void handleInvalidRequest(HttpServletResponse response, PrintWriter out, String message) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("message", message);
        out.print(gson.toJson(error));
    }

    private void handleError(HttpServletResponse response, PrintWriter out, Exception e) {
        handleError(response, out, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private void handleError(HttpServletResponse response, PrintWriter out, int statusCode, String message) {
        response.setStatus(statusCode);
        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("message", message);
        out.print(gson.toJson(error));
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
