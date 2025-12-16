package com.ecommerce.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;

    private boolean used;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "user_type", nullable = false)
    private String userType; // ADMIN / BUYER / SELLER / SHIPPER

    public PasswordResetToken() {}

    public PasswordResetToken(String token, String userId, String userType, Date expiredAt) {
        this.token = token;
        this.userId = userId;
        this.userType = userType;
        this.createdAt = new Date();
        this.expiredAt = expiredAt;
        this.used = false;
    }

    public boolean isExpired() {
        return expiredAt.before(new Date());
    }

    public void markUsed() {
        this.used = true;
    }
    
     public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isUsed() {         
        return used;
    }


}
