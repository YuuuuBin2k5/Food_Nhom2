package business;

import business.Product;
import business.User;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T15:24:26", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Review.class)
public class Review_ { 

    public static volatile SingularAttribute<Review, Product> product;
    public static volatile SingularAttribute<Review, Date> reviewDate;
    public static volatile SingularAttribute<Review, Integer> rating;
    public static volatile SingularAttribute<Review, String> comment;
    public static volatile SingularAttribute<Review, Long> reviewId;
    public static volatile SingularAttribute<Review, User> buyer;

}