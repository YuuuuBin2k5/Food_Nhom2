package com.ecommerce.entity;

import com.ecommerce.entity.Order;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T17:42:19", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Shipper.class)
public class Shipper_ extends User_ {

    public static volatile SingularAttribute<Shipper, Boolean> isAvailable;
    public static volatile ListAttribute<Shipper, Order> assignedOrders;

}