package business;

import business.Product;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T15:24:26", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Seller.class)
public class Seller_ extends User_ {

    public static volatile SingularAttribute<Seller, Double> revenue;
    public static volatile SingularAttribute<Seller, Float> rating;
    public static volatile SingularAttribute<Seller, String> shopName;
    public static volatile ListAttribute<Seller, Product> products;

}