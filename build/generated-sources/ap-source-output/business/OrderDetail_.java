package business;

import business.Order;
import business.Product;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T15:24:26", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(OrderDetail.class)
public class OrderDetail_ { 

    public static volatile SingularAttribute<OrderDetail, Product> product;
    public static volatile SingularAttribute<OrderDetail, Integer> quantity;
    public static volatile SingularAttribute<OrderDetail, Double> priceAtPurchase;
    public static volatile SingularAttribute<OrderDetail, Long> orderDetailId;
    public static volatile SingularAttribute<OrderDetail, Order> order;

}