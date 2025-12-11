package com.ecommerce.dto;

public class CheckoutResponse {
    
    private boolean success;
    private String orderId;
    private String message;
    
    public CheckoutResponse() {
    }
    
    public static CheckoutResponse success(String orderId) {
        CheckoutResponse response = new CheckoutResponse();
        response.success = true;
        response.orderId = orderId;
        response.message = "Checkout successful!";
        return response;
    }
    
    public static CheckoutResponse error(String errorMessage) {
        CheckoutResponse response = new CheckoutResponse();
        response.success = false;
        response.orderId = null;
        response.message = errorMessage;
        return response;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
