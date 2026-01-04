<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title != null ? param.title : 'FreshSave'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-white">
    <!-- Include Sidebar -->
    <jsp:include page="sidebar.jsp">
        <jsp:param name="currentPath" value="${param.currentPath}" />
    </jsp:include>
    
    <!-- Main Content -->
    <main style="margin-top: 96px;">
        <!-- Content will be inserted here by the calling page -->
        <jsp:doBody/>
    </main>
    
    <!-- Include Footer -->
    <jsp:include page="footer.jsp" />
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
