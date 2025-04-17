<%--
    Document   : signup
    Created on : 4 Apr 2025, 4:16:56 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sign Up Page</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/login_signup/signup.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
    </head>
    <header>
        <%@include file="../../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Profile</h2>
        </div>

        <div class="container">
            <div class="box">
                <div class="image">
                    <img src="${pageContext.request.contextPath}/pic/logo/logo_2.png">
                </div>
                <div class="form">
                    <h1 class="signup-title">Sign Up</h1>

                    <form method="post">
                        <div class="input">
                            <label for="username">Username</label>
                            <div class="icon">
                                <i class="fa-solid fa-user"></i>
                                <input type="text" id="username" placeholder="Enter username" required>
                            </div>
                        </div>

                        <div class="input">
                            <label for="tel">Phone</label>
                            <div class="icon">
                                <i class="fa-solid fa-phone"></i>
                                <input type="tel" id="tel" placeholder="Enter phone number" required>
                            </div>
                        </div>

                        <div class="input">
                            <label for="email">Email</label>
                            <div class="icon">
                                <i class="fa-solid fa-envelope"></i>
                                <input type="email" id="email" placeholder="Enter email" required>
                            </div>
                        </div>

                        <div class="input">
                            <label for="password">Password</label>
                            <div class="icon">
                                <i class="fa-solid fa-key"></i>
                                <input type="password" id="password" placeholder="Enter password" required>
                            </div>
                        </div>

                        <div class="input">
                            <label for="confirm-password">Confirm Password</label>
                            <div class="icon">
                                <i class="fa-solid fa-key"></i>
                                <input type="password" id="confirm-password" placeholder="Confirm password" required>
                            </div>
                        </div>

                        <button type="submit" class="button">Sign Up</button>
                    </form>

                    <!-- back to previous page -->
                    <div class="back">
                        Already have an account? <a href="login.jsp">Login</a>
                    </div>
                </div>
            </div>
        </div>

    </body>
    <footer>
        <%@include file="../../h&f/footer.jsp" %>
    </footer>
</html>