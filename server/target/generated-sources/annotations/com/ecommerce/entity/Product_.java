package com.ecommerce.entity;

import com.ecommerce.entity.ProductStatus;
import com.ecommerce.entity.Seller;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T17:42:19", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Product.class)
public class Product_ { 

    public static volatile SingularAttribute<Product, Seller> seller;
    public static volatile SingularAttribute<Product, Integer> quantity;
    public static volatile SingularAttribute<Product, Long> productId;
    public static volatile SingularAttribute<Product, Double> originalPrice;
    public static volatile SingularAttribute<Product, Double> salePrice;
    public static volatile SingularAttribute<Product, Boolean> isVerified;
    public static volatile SingularAttribute<Product, String> name;
    public static volatile SingularAttribute<Product, String> description;
    public static volatile SingularAttribute<Product, Date> manufactureDate;
    public static volatile SingularAttribute<Product, Date> expirationDate;
    public static volatile SingularAttribute<Product, ProductStatus> status;

}