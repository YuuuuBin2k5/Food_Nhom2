<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thu nhập - Shipper</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipper.css">
</head>
<body class="bg-white shipper-page">
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/shipper/earnings"/>
    </jsp:include>

    <main class="shipper-main">
        
        <!-- Header -->
        <div style="margin-bottom: 2rem;">
            <h2 style="font-size: 1.8rem; color: #1a202c; font-weight: 700; margin: 0;">💰 Thu nhập</h2>
            <p style="color: #718096; margin: 0.5rem 0 0 0;">Thống kê thu nhập của bạn</p>
        </div>

        <!-- Today Earnings Card -->
        <div style="background: linear-gradient(135deg, #1a202c 0%, #2d3748 100%); border-radius: 1rem; padding: 2rem; color: white; margin-bottom: 2rem; box-shadow: 0 10px 25px rgba(0,0,0,0.2);">
            <p style="color: #a0aec0; margin: 0 0 0.5rem 0; font-size: 0.875rem;">Thu nhập hôm nay</p>
            <h1 style="font-size: 3rem; font-weight: 900; margin: 0 0 1.5rem 0;">
                <fmt:formatNumber value="${todayEarnings != null ? todayEarnings : 0}" type="number" maxFractionDigits="0"/>₫
            </h1>
            
            <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem;">
                <div style="background: rgba(255,255,255,0.1); padding: 1rem; border-radius: 0.75rem;">
                    <p style="color: #a0aec0; font-size: 0.75rem; margin: 0 0 0.25rem 0;">Số chuyến</p>
                    <p style="font-size: 1.5rem; font-weight: 700; margin: 0;">${todayTrips != null ? todayTrips : 0}</p>
                </div>
                <div style="background: rgba(255,255,255,0.1); padding: 1rem; border-radius: 0.75rem;">
                    <p style="color: #a0aec0; font-size: 0.75rem; margin: 0 0 0.25rem 0;">Phí/chuyến</p>
                    <p style="font-size: 1.5rem; font-weight: 700; margin: 0;">15,000₫</p>
                </div>
                <div style="background: rgba(255,255,255,0.1); padding: 1rem; border-radius: 0.75rem;">
                    <p style="color: #a0aec0; font-size: 0.75rem; margin: 0 0 0.25rem 0;">Tuần này</p>
                    <p style="font-size: 1.5rem; font-weight: 700; margin: 0;">
                        <fmt:formatNumber value="${weeklyTotal != null ? weeklyTotal : 0}" type="number" maxFractionDigits="0"/>₫
                    </p>
                </div>
            </div>
        </div>

        <!-- Weekly Chart -->
        <div style="background: white; border-radius: 0.75rem; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 1.5rem; margin-bottom: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                <h3 style="margin: 0; color: #1a202c; display: flex; align-items: center; gap: 0.5rem;">
                    📊 Biểu đồ tuần
                </h3>
                <span style="color: #718096; font-size: 0.875rem;">
                    Tổng: <fmt:formatNumber value="${weeklyTotal != null ? weeklyTotal : 0}" type="number" maxFractionDigits="0"/>₫
                </span>
            </div>
            
            <div style="display: flex; align-items: flex-end; justify-content: space-between; height: 160px; gap: 0.5rem;">
                <c:forEach var="i" begin="0" end="6">
                    <div style="flex: 1; display: flex; flex-direction: column; align-items: center;">
                        <div style="width: 100%; background: ${i == 4 ? '#805ad5' : '#e2e8f0'}; border-radius: 4px 4px 0 0; 
                                    height: ${i == 0 ? '20%' : i == 1 ? '45%' : i == 2 ? '35%' : i == 3 ? '60%' : i == 4 ? '80%' : '0%'};
                                    min-height: 4px;"></div>
                        <span style="font-size: 0.75rem; margin-top: 0.5rem; color: ${i == 4 ? '#805ad5' : '#718096'}; font-weight: ${i == 4 ? '700' : '500'};">
                            ${i == 0 ? 'T2' : i == 1 ? 'T3' : i == 2 ? 'T4' : i == 3 ? 'T5' : i == 4 ? 'T6' : i == 5 ? 'T7' : 'CN'}
                        </span>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Quick Actions -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
            <a href="${pageContext.request.contextPath}/shipper/history" 
               style="display: flex; align-items: center; justify-content: center; gap: 0.75rem; background: white; color: #3182ce; border: 2px solid #3182ce; padding: 1rem 1.5rem; border-radius: 0.5rem; text-decoration: none; font-weight: 600;">
                📋 Xem lịch sử giao hàng
            </a>
            <a href="${pageContext.request.contextPath}/shipper/orders" 
               style="display: flex; align-items: center; justify-content: center; gap: 0.75rem; background: linear-gradient(to right, #805ad5, #6b46c1); color: white; padding: 1rem 1.5rem; border-radius: 0.5rem; text-decoration: none; font-weight: 600;">
                📦 Nhận đơn mới
            </a>
        </div>
    </main>

    <jsp:include page="../common/footer.jsp"/>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
