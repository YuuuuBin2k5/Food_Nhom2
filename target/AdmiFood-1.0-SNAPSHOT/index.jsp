<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng Nhập Hệ Thống</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/main.css'/>">
</head>
<body>

    <div class="login-container">
        <div class="login-title">Food Rescue Admin</div>
        
        <c:if test="${not empty message}">
            <div class="error-msg">${message}</div>
        </c:if>

        <form action="admin" method="post">
            <input type="hidden" name="action" value="login">
            
            <div style="text-align: left; margin-bottom: 5px; font-weight: bold; color: #555;">Username</div>
            <input type="text" name="username" placeholder="Nhập username (admin)" required>
            
            <div style="text-align: left; margin-bottom: 5px; font-weight: bold; color: #555; margin-top: 15px;">Password</div>
            <input type="password" name="password" placeholder="Nhập password (123)" required>
            
            <button type="submit" class="btn btn-primary">ĐĂNG NHẬP</button>
        </form>
    </div>

</body>
</html>