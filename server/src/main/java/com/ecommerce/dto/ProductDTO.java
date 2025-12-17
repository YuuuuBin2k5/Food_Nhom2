package com.ecommerce.dto;

import java.util.Date;
import com.ecommerce.entity.ProductStatus;

public class ProductDTO {
    public Long productId;
    public String name;
    public String description;
    public double originalPrice;
    public double salePrice;
    public int quantity;
    public Date expirationDate;
    public Date manufactureDate;
    public ProductStatus status; 
    public ProductDTO() {
    }
}