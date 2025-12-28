<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <jsp:include page="header.jsp" />

        <div class="container" style="max-width: 400px; margin-top: 50px;">
            <div style="background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                <h2 class="text-center">Login</h2>

                <c:if test="${not empty error}">
                    <div class="text-danger text-center mb-20">
                        ${error}
                    </div>
                </c:if>

                <form action="login" method="POST">
                    <input type="hidden" name="redirect" value="${not empty param.redirect ? param.redirect : ''}">

                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" class="form-control" required
                            value="${not empty email ? email : ''}">
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" class="form-control" required>
                    </div>

                    <button type="submit" class="btn btn-block" style="margin-top: 20px;">Login</button>

                    <div class="text-center mt-20">
                        <small>Don't have an account? <a href="register">Register</a></small>
                    </div>
                </form>
            </div>
        </div>

        <jsp:include page="footer.jsp" />