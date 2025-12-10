package com.ecommerce.entity;

import com.ecommerce.entity.Role;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-12-10T17:42:19", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(User.class)
public abstract class User_ { 

    public static volatile SingularAttribute<User, String> password;
    public static volatile SingularAttribute<User, String> phoneNumber;
    public static volatile SingularAttribute<User, String> address;
    public static volatile SingularAttribute<User, Role> role;
    public static volatile SingularAttribute<User, Date> createdDate;
    public static volatile SingularAttribute<User, String> fullName;
    public static volatile SingularAttribute<User, Boolean> isBanned;
    public static volatile SingularAttribute<User, String> userId;
    public static volatile SingularAttribute<User, String> email;

}