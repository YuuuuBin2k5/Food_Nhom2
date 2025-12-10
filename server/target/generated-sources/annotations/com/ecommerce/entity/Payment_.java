package com.ecommerce.entity;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.PaymentMethod;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T17:42:19", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Payment.class)
public class Payment_ { 

    public static volatile SingularAttribute<Payment, Double> amount;
    public static volatile SingularAttribute<Payment, PaymentMethod> method;
    public static volatile SingularAttribute<Payment, Long> paymentId;
    public static volatile SingularAttribute<Payment, Date> paymentDate;
    public static volatile SingularAttribute<Payment, Order> order;

}