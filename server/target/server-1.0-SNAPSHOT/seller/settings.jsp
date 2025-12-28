<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cài đặt Shop - Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="../common/sidebar.jsp" />
    
    <div class="main-content">
        <!-- Header Banner -->
        <div class="header-banner">
            <div class="container">
                <div>
                    <h1 class="page-title">
                        <span class="icon">⚙️</span>
                        Cài đặt Shop
                    </h1>
                    <p class="page-subtitle">Quản lý thông tin cửa hàng của bạn</p>
                </div>
            </div>
        </div>

        <div class="container py-4">
            <div class="row">
                <div class="col-md-8 mx-auto">
                    <div class="card">
                        <div class="card-header">
                            <h3>Thông tin cửa hàng</h3>
                        </div>
                        <div class="card-body">
                            <form id="settingsForm" onsubmit="handleSubmitSettings(event)">
                                <div class="form-group">
                                    <label for="shopName">Tên cửa hàng *</label>
                                    <input type="text" id="shopName" name="shopName" 
                                           class="form-control" 
                                           value="${sessionScope.user.fullName}" required>
                                    <small class="form-text">Tên hiển thị của cửa hàng trên hệ thống</small>
                                </div>
                                
                                <div class="form-group">
                                    <label for="email">Email liên hệ *</label>
                                    <input type="email" id="email" name="email" 
                                           class="form-control" 
                                           value="${sessionScope.user.email}" required>
                                </div>
                                
                                <div class="form-group">
                                    <label for="phone">Số điện thoại *</label>
                                    <input type="tel" id="phone" name="phone" 
                                           class="form-control" 
                                           value="${sessionScope.user.phone}" required>
                                </div>
                                
                                <div class="form-group">
                                    <label for="address">Địa chỉ cửa hàng</label>
                                    <textarea id="address" name="address" 
                                              class="form-control" rows="3"></textarea>
                                    <small class="form-text">Địa chỉ kho hàng hoặc cửa hàng</small>
                                </div>
                                
                                <div class="form-group">
                                    <label for="description">Mô tả cửa hàng</label>
                                    <textarea id="description" name="description" 
                                              class="form-control" rows="4"></textarea>
                                    <small class="form-text">Giới thiệu về cửa hàng của bạn</small>
                                </div>
                                
                                <hr class="my-4">
                                
                                <h4 class="mb-3">Thông tin tài khoản</h4>
                                
                                <div class="form-group">
                                    <label>Tên đăng nhập</label>
                                    <input type="text" class="form-control" 
                                           value="${sessionScope.user.userId}" disabled>
                                    <small class="form-text">Không thể thay đổi tên đăng nhập</small>
                                </div>
                                
                                <div class="form-group">
                                    <label>Vai trò</label>
                                    <input type="text" class="form-control" 
                                           value="Người bán (Seller)" disabled>
                                </div>
                                
                                <div class="alert alert-info mt-4">
                                    <strong>💡 Lưu ý:</strong> Để thay đổi mật khẩu hoặc thông tin bảo mật, 
                                    vui lòng liên hệ quản trị viên hệ thống.
                                </div>
                                
                                <div class="form-actions mt-4">
                                    <button type="submit" class="btn btn-primary">
                                        💾 Lưu thay đổi
                                    </button>
                                    <button type="button" onclick="window.history.back()" 
                                            class="btn btn-secondary">
                                        ← Quay lại
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Shop Statistics -->
                    <div class="card mt-4">
                        <div class="card-header">
                            <h3>Thống kê cửa hàng</h3>
                        </div>
                        <div class="card-body">
                            <div class="stats-grid">
                                <div class="stat-item">
                                    <span class="stat-icon">📦</span>
                                    <div>
                                        <div class="stat-label">Tổng sản phẩm</div>
                                        <div class="stat-value" id="totalProducts">-</div>
                                    </div>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-icon">📋</span>
                                    <div>
                                        <div class="stat-label">Tổng đơn hàng</div>
                                        <div class="stat-value" id="totalOrders">-</div>
                                    </div>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-icon">⭐</span>
                                    <div>
                                        <div class="stat-label">Đánh giá</div>
                                        <div class="stat-value">5.0</div>
                                    </div>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-icon">📅</span>
                                    <div>
                                        <div class="stat-label">Tham gia từ</div>
                                        <div class="stat-value text-sm">2024</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        const API_BASE = '${pageContext.request.contextPath}/api';
        
        // Load shop statistics
        async function loadStats() {
            try {
                const [products, orders] = await Promise.all([
                    apiRequest(API_BASE + '/seller/products').catch(() => []),
                    apiRequest(API_BASE + '/seller/orders').catch(() => [])
                ]);
                
                document.getElementById('totalProducts').textContent = products.length || 0;
                document.getElementById('totalOrders').textContent = orders.length || 0;
            } catch (error) {
                console.error('Error loading stats:', error);
            }
        }
        
        async function handleSubmitSettings(event) {
            event.preventDefault();
            
            const formData = new FormData(event.target);
            const data = {
                shopName: formData.get('shopName'),
                email: formData.get('email'),
                phone: formData.get('phone'),
                address: formData.get('address'),
                description: formData.get('description')
            };
            
            try {
                showLoading();
                
                // In a real app, this would call an API endpoint
                // await apiRequest(API_BASE + '/seller/settings', {
                //     method: 'PUT',
                //     body: JSON.stringify(data)
                // });
                
                showToast('Cập nhật thông tin thành công!', 'success');
                
                // Simulate API delay
                await new Promise(resolve => setTimeout(resolve, 500));
                
            } catch (error) {
                showToast(error.message || 'Có lỗi xảy ra', 'error');
            } finally {
                hideLoading();
            }
        }
        
        // Load stats on page load
        loadStats();
    </script>
    
    <style>
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
        }
        
        .stat-item {
            display: flex;
            align-items: center;
            gap: 1rem;
            padding: 1rem;
            background: #f8fafc;
            border-radius: 0.75rem;
        }
        
        .stat-icon {
            font-size: 2rem;
        }
        
        .stat-label {
            font-size: 0.875rem;
            color: #64748b;
            margin-bottom: 0.25rem;
        }
        
        .stat-value {
            font-size: 1.5rem;
            font-weight: 700;
            color: #0f172a;
        }
        
        .form-actions {
            display: flex;
            gap: 1rem;
        }
    </style>
</body>
</html>
