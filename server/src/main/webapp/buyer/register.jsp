<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="header.jsp" />

        <div class="container" style="max-width: 450px; margin-top: 50px; margin-bottom: 50px;">
            <div style="background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                <h2 class="text-center" style="margin-bottom: 20px;">Create Account</h2>

                <c:if test="${not empty error}">
                    <div class="text-danger text-center mb-20"
                        style="background: #fde8e8; padding: 10px; border-radius: 5px; color: #c0392b;">
                        ${error}
                    </div>
                </c:if>

                <form action="register" method="POST">

                    <div class="form-group">
                        <label for="fullName">Full Name</label>
                        <input type="text" id="fullName" name="fullName" class="form-control" required
                            value="${not empty fullName ? fullName : ''}" placeholder="Enter your full name">
                    </div>

                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control" required
                            value="${not empty email ? email : ''}" placeholder="Enter your email">
                    </div>

                    <div class="form-group">
                        <label for="phoneNumber">Phone Number</label>
                        <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control" required
                            value="${not empty phoneNumber ? phoneNumber : ''}" placeholder="Enter your phone number">
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" class="form-control" required
                            placeholder="Create a password">
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required
                            placeholder="Confirm your password">
                    </div>

                    <button type="submit" class="btn btn-block"
                        style="margin-top: 20px; font-weight: bold;">Register</button>

                    <div class="text-center mt-20">
                        <small>Already have an account? <a href="login" style="color: #e74c3c; font-weight: bold;">Login
                                here</a></small>
                    </div>
                </form>
            </div>
        </div>

        <jsp:include page="footer.jsp" />