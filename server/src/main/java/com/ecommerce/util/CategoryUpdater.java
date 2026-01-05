package com.ecommerce.util;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Utility to bulk update product categories
 * Run this ONCE to fix existing products without categories
 */
public class CategoryUpdater {

    public static void main(String[] args) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            // Get all products without category
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.category IS NULL", 
                Product.class
            );
            List<Product> products = query.getResultList();
            
            System.out.println("Found " + products.size() + " products without category");
            
            int updated = 0;
            for (Product p : products) {
                ProductCategory cat = detectCategory(p.getName());
                if (cat != null) {
                    p.setCategory(cat);
                    updated++;
                    System.out.println("  Updated: " + p.getName() + " → " + cat.getDisplayName());
                }
            }
            
            em.getTransaction().commit();
            
            System.out.println("\n✅ Updated " + updated + " products");
            
            // Print summary
            printCategorySummary(em);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    /**
     * Detect category based on product name
     */
    private static ProductCategory detectCategory(String name) {
        if (name == null) return ProductCategory.OTHER;
        
        String lower = name.toLowerCase();
        
        // VEGETABLES
        if (lower.contains("rau") || lower.contains("củ") || 
            lower.contains("cà chua") || lower.contains("khoai") ||
            lower.contains("cải") || lower.contains("đậu") || lower.contains("bí")) {
            return ProductCategory.VEGETABLES;
        }
        
        // FRUITS
        if (lower.contains("táo") || lower.contains("cam") || 
            lower.contains("chuối") || lower.contains("xoài") ||
            lower.contains("dưa") || lower.contains("nho") || 
            lower.contains("dứa") || lower.contains("ổi")) {
            return ProductCategory.FRUITS;
        }
        
        // MEAT
        if (lower.contains("thịt") || lower.contains("heo") || 
            lower.contains("gà") || lower.contains("bò") ||
            lower.contains("vịt") || lower.contains("lợn")) {
            return ProductCategory.MEAT;
        }
        
        // SEAFOOD
        if (lower.contains("cá") || lower.contains("tôm") || 
            lower.contains("mực") || lower.contains("ghẹ") ||
            lower.contains("cua") || lower.contains("ngao")) {
            return ProductCategory.SEAFOOD;
        }
        
        // DAIRY
        if (lower.contains("sữa") || lower.contains("pho mai") || 
            lower.contains("yogurt") || lower.contains("bơ sữa")) {
            return ProductCategory.DAIRY;
        }
        
        // BAKERY
        if (lower.contains("bánh mì") || lower.contains("bánh ngọt") || 
            lower.contains("bánh quy")) {
            return ProductCategory.BAKERY;
        }
        
        // SNACKS
        if (lower.contains("snack") || lower.contains("kẹo") || 
            lower.contains("chocolate") || lower.contains("bim bim")) {
            return ProductCategory.SNACKS;
        }
        
        // BEVERAGES
        if (lower.contains("nước") || lower.contains("trà") || 
            lower.contains("cà phê") || lower.contains("coffee") ||
            lower.contains("coca") || lower.contains("pepsi")) {
            return ProductCategory.BEVERAGES;
        }
        
        // FROZEN
        if (lower.contains("đông lạnh") || lower.contains("frozen") || 
            lower.contains(" kem ")) {
            return ProductCategory.FROZEN;
        }
        
        return ProductCategory.OTHER;
    }
    
    /**
     * Print category summary
     */
    private static void printCategorySummary(EntityManager em) {
        System.out.println("\n📊 Category Summary:");
        System.out.println("═".repeat(50));
        
        for (ProductCategory cat : ProductCategory.values()) {
            Long count = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.category = :cat AND p.status = 'ACTIVE'", 
                Long.class
            )
            .setParameter("cat", cat)
            .getSingleResult();
            
            if (count > 0) {
                System.out.printf("%s %-20s: %3d products\n", 
                    cat.getEmoji(), cat.getDisplayName(), count);
            }
        }
        
        System.out.println("═".repeat(50));
    }
}
