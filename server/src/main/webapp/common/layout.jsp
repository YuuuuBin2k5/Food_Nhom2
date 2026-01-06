<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#4CAF50">
    <title>${param.title != null ? param.title : 'FreshSave'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
    <link rel="apple-touch-icon" href="${pageContext.request.contextPath}/images/icon-192.png">
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
    
    <!-- Đăng ký Service Worker cho PWA -->
    <script>
        if ('serviceWorker' in navigator) {
            window.addEventListener('load', () => {
                navigator.serviceWorker.register('${pageContext.request.contextPath}/sw.js')
                    .then((reg) => console.log('Service Worker registered'))
                    .catch((err) => console.log('Service Worker registration failed:', err));
            });
        }
    </script>
</body>
</html>
