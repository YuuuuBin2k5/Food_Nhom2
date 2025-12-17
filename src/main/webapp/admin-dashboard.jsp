<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/main.css'/>">
</head>
<body>

    <%-- Lấy currentAction từ request attribute (ưu tiên) hoặc param --%>
    <c:set var="currentAction" value="${not empty requestScope.currentAction ? requestScope.currentAction : param.action}" />
    
    <%-- Nếu không có action, mặc định là viewLog --%>
    <c:if test="${empty currentAction or currentAction == 'login'}">
        <c:set var="currentAction" value="viewLog" />
    </c:if>

    <div class="sidebar">
        <a href="admin?action=viewLog" 
           class="menu-item ${currentAction == 'viewLog' ? 'active' : ''}">
            User Log
        </a>
        
        <a href="admin?action=viewSeller" 
           class="menu-item ${currentAction == 'viewSeller' ? 'active' : ''}">
            Duyệt Seller
        </a>
        
        <a href="admin?action=viewProduct" 
           class="menu-item ${currentAction == 'viewProduct' ? 'active' : ''}">
            Duyệt Product
        </a>
        
        <a href="admin?action=viewBanUser" 
           class="menu-item ${currentAction == 'viewBanUser' ? 'active' : ''}">
            Ban User
        </a>
        
        <div style="flex: 1;"></div> 
        
        <a href="admin?action=logout" class="menu-item logout">
            Đăng xuất
        </a>
    </div>

    <div class="main-content">
        <%-- Dùng jsp:include để nhúng file con tương ứng vào --%>
        <c:choose>
            <c:when test="${currentAction == 'viewLog'}">
                <jsp:include page="admin/log.jsp" />
            </c:when>

            <c:when test="${currentAction == 'viewSeller'}">
                <jsp:include page="admin/seller.jsp" />
            </c:when>

            <c:when test="${currentAction == 'viewProduct'}">
                <jsp:include page="admin/product.jsp" />
            </c:when>

            <c:when test="${currentAction == 'viewBanUser'}">
                <jsp:include page="admin/ban.jsp" />
            </c:when>
            
            <c:otherwise>
                <h3>Chào mừng Admin! Vui lòng chọn chức năng bên trái.</h3>
            </c:otherwise>
        </c:choose>
    </div>

</body>
</html>