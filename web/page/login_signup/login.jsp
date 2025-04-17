<%--
    Document   : login
    Created on : 1 Apr 2025, 3:19:40 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if ("POST".equalsIgnoreCase(request.getMethod())) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            session.setAttribute("user", username);
            session.setAttribute("loginSuccess", "true");
            response.sendRedirect("../../index.jsp");
            return;
        } else {
            request.setAttribute("error", "Username and password cannot be empty");
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/login_signup/login.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
    </head>
    <header>
        <%@include file="../../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Login</h2>
        </div>

        <!-- container -->
        <div class="container">
            <!<!-- box -->
            <div class="box">
                <!-- image -->
                <div class="image">
                    <img src="${pageContext.request.contextPath}/pic/logo/logo_2.png">
                </div>

                <!-- form -->
                <div class="form">
                    <h1 class="login-title">Login</h1>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="error-message">
                        <%= request.getAttribute("error") %>
                    </div>
                    <% } %>

                    <form method="POST">
                        <div class="detail">
                            <label for="username">Username</label>
                            <div class="icon">
                                <i class="fa-solid fa-user"></i>
                                <input type="text" id="username" name="username" placeholder="Enter username" required>
                            </div>
                        </div>

                        <div class="detail">
                            <label for="password">Password</label>
                            <div class="icon">
                                <i class="fa-solid fa-key"></i>
                                <input type="password" id="password" name="password" placeholder="Enter password" required>
                            </div>
                        </div>

                        <div class="forgot-passwd">
                            <a href="forgot_password.jsp">Forgot password?</a>
                        </div>

                        <button type="submit" class="button">Login</button>
                    </form>

                    <!-- go to sign up page -->
                    <div class="signup">
                        Don't have an account? <a href="signup.jsp">Sign up</a>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="../../h&f/footer.jsp" %>
    </footer>
</html>
