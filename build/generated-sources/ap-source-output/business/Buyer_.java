package business;

import business.Order;
import business.Review;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T15:24:26", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Buyer.class)
public class Buyer_ extends User_ {

    public static volatile ListAttribute<Buyer, Review> reviews;
    public static volatile ListAttribute<Buyer, Order> orders;
    public static volatile ListAttribute<Buyer, String> savedAddresses;

}