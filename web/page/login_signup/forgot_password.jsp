<%--
    Document   : login
    Created on : 1 Apr 2025, 3:19:40 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Forgot Password</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/login_signup/forgot_password.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
    </head>
    <header>
        <%@include file="../../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Forgot Password</h2>
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
                    <h1 class="title-form">Reset Your Password</h1>

                    <form method="POST">
                        <div id="email-form">
                            <div class="detail">
                                <label for="email">Email Address</label>
                                <div class="icon">
                                    <i class="fas fa-envelope"></i>
                                    <input type="email" id="email" name="email" placeholder="Enter your email" required>
                                </div>
                            </div>

                            <div class="option">
                                Forgot your email? <span onclick="toggleForm()">Use username instead</span>
                            </div>
                        </div>

                        <div id="username-form" style="display: none;">
                            <div class="detail">
                                <label for="username">Username</label>
                                <div class="icon">
                                    <i class="fas fa-user"></i>
                                    <input type="text" id="username" name="username" placeholder="Enter your username">
                                </div>
                            </div>

                            <div class="option">
                                Prefer to use email? <span onclick="toggleForm()">Use email instead</span>
                            </div>
                        </div>

                        <button action="set_password.jsp" type="submit" class="button">Reset Password</button>
                    </form>

                    <!-- back to previous page -->
                    <div class="back">
                        <a href="login.jsp">Back to Login</a>
                    </div>
                </div>
            </div>
        </div>

                <script>
                    function toggleForm() {
                        const emailForm = document.getElementById('email-form');
                        const usernameForm = document.getElementById('username-form');

                        if (emailForm.style.display === 'none') {
                            emailForm.style.display = 'block';
                            usernameForm.style.display = 'none';
                            document.getElementById('email').required = true;
                            document.getElementById('username').required = false;
                        } else {
                            emailForm.style.display = 'none';
                            usernameForm.style.display = 'block';
                            document.getElementById('email').required = false;
                            document.getElementById('username').required = true;
                        }
                    }
                </script>
    </body>
    <footer>
        <%@include file="../../h&f/footer.jsp" %>
    </footer>
</html>