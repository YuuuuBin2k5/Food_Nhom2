package business;

import business.OrderDetail;
import business.OrderStatus;
import business.Payment;
import business.Shipper;
import business.User;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T15:24:26", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Order.class)
public class Order_ { 

    public static volatile SingularAttribute<Order, Shipper> shipper;
    public static volatile ListAttribute<Order, OrderDetail> orderDetails;
    public static volatile SingularAttribute<Order, Long> orderId;
    public static volatile SingularAttribute<Order, String> shippingAddress;
    public static volatile SingularAttribute<Order, Payment> payment;
    public static volatile SingularAttribute<Order, Date> orderDate;
    public static volatile SingularAttribute<Order, OrderStatus> status;
    public static volatile SingularAttribute<Order, User> buyer;

}