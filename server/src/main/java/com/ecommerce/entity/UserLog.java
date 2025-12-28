package com.ecommerce.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_logs")
public class UserLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    // Ai thực hiện hành động
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role userRole;

    // Loại hành động
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private ActionType action;

    // Mô tả chi tiết
    @Column(name = "description", length = 500)
    private String description;

    // Đối tượng bị tác động (nếu có)
    @Column(name = "target_id")
    private String targetId;

    @Column(name = "target_type")
    private String targetType;

    // Admin thực hiện hành động (cho các action cần admin như ban, approve...)
    @Column(name = "admin_id")  
    private String adminId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    // Constructors
    public UserLog() {
        this.createdAt = new Date();
    }

    public UserLog(String userId, Role userRole, ActionType action, String description) {
        this.userId = userId;
        this.userRole = userRole;
        this.action = action;
        this.description = description;
        this.createdAt = new Date();
    }

    public UserLog(String userId, Role userRole, ActionType action, String description, 
                   String targetId, String targetType) {
        this.userId = userId;
        this.userRole = userRole;
        this.action = action;
        this.description = description;
        this.targetId = targetId;
        this.targetType = targetType;
        this.createdAt = new Date();
    }

    // Constructor với adminId (cho các action do admin thực hiện)
    public UserLog(String userId, Role userRole, ActionType action, String description, 
                   String targetId, String targetType, String adminId) {
        this.userId = userId;
        this.userRole = userRole;
        this.action = action;
        this.description = description;
        this.targetId = targetId;
        this.targetType = targetType;
        this.adminId = adminId;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
