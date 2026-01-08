<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Tài khoản của tôi - FoodRescue</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/buyer/profile.css">
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap"
                    rel="stylesheet">
            </head>

            <body>
                <!-- Include Sidebar -->
                <jsp:include page="../common/sidebar.jsp">
                    <jsp:param name="currentPath" value="/settings" />
                </jsp:include>

                <main class="profile-container">
                    <!-- Profile Header -->
                    <div class="profile-header">
                        <div class="profile-header-content">
                            <div class="profile-avatar-large">
                                ${sessionScope.user.fullName.substring(0, 1).toUpperCase()}
                            </div>
                            <div class="profile-header-info">
                                <h1>${sessionScope.user.fullName}</h1>
                                <p class="profile-email">${sessionScope.user.email}</p>
                                <span class="role-badge">${sessionScope.user.role}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Profile Content -->
                    <div class="profile-content">
                        <!-- Personal Information -->
                        <div class="info-section">
                            <div class="section-header">
                                <h2>
                                    <svg class="section-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                    </svg>
                                    Thông tin cá nhân
                                </h2>                
                            </div>
                            <!-- View Mode -->
                            <div class="info-grid" id="viewMode">
                                <div class="info-item">
                                    <label>Họ và tên</label>
                                    <p id="displayFullName">${sessionScope.user.fullName}</p>
                                </div>

                                <div class="info-item">
                                    <label>Email</label>
                                    <p>${sessionScope.user.email}</p>
                                </div>

                                <div class="info-item">
                                    <label>Số điện thoại</label>
                                    <p id="displayPhone">${not empty sessionScope.user.phoneNumber ?
                                        sessionScope.user.phoneNumber : 'Chưa cập nhật'}</p>
                                </div>

                                <div class="info-item">
                                    <label>Địa chỉ</label>
                                    <p id="displayAddress">${not empty sessionScope.user.address ?
                                        sessionScope.user.address : 'Chưa cập nhật'}</p>
                                </div>
                            </div>

                            <!-- Edit Mode -->
                            <form class="info-grid" id="editMode" style="display: none;" onsubmit="saveProfile(event)">
                                <div class="info-item">
                                    <label>Họ và tên</label>
                                    <input type="text" class="form-input" id="editFullName"
                                        value="${sessionScope.user.fullName}" required>
                                </div>

                                <div class="info-item">
                                    <label>Email (không thể sửa)</label>
                                    <input type="email" class="form-input" value="${sessionScope.user.email}" disabled>
                                </div>

                                <div class="info-item">
                                    <label>Số điện thoại</label>
                                    <input type="tel" class="form-input" id="editPhone"
                                        value="${sessionScope.user.phoneNumber}" placeholder="Nhập số điện thoại">
                                </div>

                                <div class="info-item">
                                    <label>Địa chỉ</label>
                                    <input type="text" class="form-input" id="editAddress"
                                        value="${sessionScope.user.address}" placeholder="Nhập địa chỉ">
                                </div>

                                <div class="edit-actions">
                                    <button type="submit" class="btn-save">Lưu thay đổi</button>
                                    <button type="button" class="btn-cancel" onclick="cancelEdit()">Hủy</button>
                                </div>
                            </form>
                        </div>

                        <!-- Account Information -->
                        <div class="info-section">
                            <div class="section-header">
                                <h2>
                                    <svg class="section-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                                    </svg>
                                    Thông tin tài khoản
                                </h2>
                            </div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <label>Mã tài khoản</label>
                                    <p class="user-id">${sessionScope.user.userId}</p>
                                </div>

                                <div class="info-item">
                                    <label>Ngày tham gia</label>
                                    <p>
                                        <fmt:formatDate value="${sessionScope.user.createdDate}" pattern="dd/MM/yyyy" />
                                    </p>
                                </div>

                                <div class="info-item">
                                    <label>Vai trò</label>
                                    <p>${sessionScope.user.role}</p>
                                </div>

                                <div class="info-item">
                                    <label>Trạng thái</label>
                                    <p class="status-active">Đang hoạt động</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>

                <script>
                    function toggleEditMode() {
                        const viewMode = document.getElementById('viewMode');
                        const editMode = document.getElementById('editMode');
                        const btn = document.querySelector('.btn-edit');

                        if (viewMode.style.display === 'none') {
                            // Cancel edit
                            viewMode.style.display = 'grid';
                            editMode.style.display = 'none';
                            btn.innerHTML = '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg> Chỉnh sửa';
                        } else {
                            // Enter edit mode
                            viewMode.style.display = 'none';
                            editMode.style.display = 'grid';
                            btn.innerHTML = '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg> Hủy';
                        }
                    }

                    function cancelEdit() {
                        toggleEditMode();
                    }

                    function saveProfile(event) {
                        event.preventDefault();

                        const fullName = document.getElementById('editFullName').value;
                        const phone = document.getElementById('editPhone').value;
                        const address = document.getElementById('editAddress').value;

                        fetch('${pageContext.request.contextPath}/update-profile', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: `fullName=` + encodeURIComponent(fullName) +
                                `&phoneNumber=` + encodeURIComponent(phone) +
                                `&address=` + encodeURIComponent(address)
                        })
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    // Update display
                                    document.getElementById('displayFullName').textContent = fullName;
                                    document.getElementById('displayPhone').textContent = phone || 'Chưa cập nhật';
                                    document.getElementById('displayAddress').textContent = address || 'Chưa cập nhật';

                                    // Update header
                                    document.querySelector('.profile-header-info h1').textContent = fullName;

                                    toggleEditMode();
                                    alert('Cập nhật thông tin thành công!');
                                } else {
                                    alert('Có lỗi xảy ra: ' + data.message);
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                alert('Có lỗi xảy ra khi cập nhật thông tin!');
                            });
                    }
                </script>
            </body>

            </html>