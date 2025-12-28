<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <title>Test Product Creation</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 40px;
            }

            .form-group {
                margin-bottom: 15px;
            }

            label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }

            input,
            textarea {
                width: 300px;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 4px;
            }

            button {
                background: #007bff;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

            button:hover {
                background: #0056b3;
            }

            .error {
                color: red;
                background: #ffe6e6;
                padding: 10px;
                border-radius: 4px;
                margin: 10px 0;
            }

            .success {
                color: green;
                background: #e6ffe6;
                padding: 10px;
                border-radius: 4px;
                margin: 10px 0;
            }
        </style>
    </head>

    <body>
        <h1>🧪 Test Product Creation</h1>

        <% if (request.getAttribute("error") !=null) { %>
            <div class="error">❌ <%= request.getAttribute("error") %>
            </div>
            <% } %>

                <% if (request.getParameter("success") !=null) { %>
                    <div class="success">✅ Product created successfully!</div>
                    <% } %>

                        <form action="${pageContext.request.contextPath}/seller/products" method="post">
                            <input type="hidden" name="action" value="create">

                            <div class="form-group">
                                <label>Tên sản phẩm:</label>
                                <input type="text" name="name" value="Test Product ${System.currentTimeMillis()}"
                                    required>
                            </div>

                            <div class="form-group">
                                <label>Mô tả:</label>
                                <textarea name="description"
                                    rows="3">Đây là sản phẩm test để kiểm tra chức năng đăng bán</textarea>
                            </div>

                            <div class="form-group">
                                <label>Giá bán (VNĐ):</label>
                                <input type="number" name="price" value="50000" min="1000" required>
                            </div>

                            <div class="form-group">
                                <label>Số lượng:</label>
                                <input type="number" name="quantity" value="10" min="1" required>
                            </div>

                            <div class="form-group">
                                <label>Ngày hết hạn:</label>
                                <input type="date" name="expirationDate" value="2025-01-15" required>
                            </div>

                            <div class="form-group">
                                <label>Link ảnh sản phẩm:</label>
                                <input type="url" name="imageUrl"
                                    value="https://via.placeholder.com/300x300?text=Test+Product">
                            </div>

                            <button type="submit">🚀 Test Create Product</button>
                        </form>

                        <hr>
                        <p><strong>Lưu ý:</strong> Trang này chỉ để test. Bạn cần đăng nhập với tài khoản seller đã được
                            approve.</p>
                        <p><a href="${pageContext.request.contextPath}/seller/products">← Quay lại trang seller
                                products</a></p>
    </body>

    </html>