<%--
    Document   : login
    Created on : 1 Apr 2025, 3:19:40 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<<<<<<< HEAD
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Login Page</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/login.css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css" />
    </head>
    <header><%@include file="components/navbar.jsp" %></header>

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
                    <img src="${pageContext.request.contextPath}/assets/logo/text.png" />
                </div>
=======
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Login Page</title>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/login.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css" />
	</head>
	<header><%@include file="components/navbar.jsp" %></header>

	<body>
		<!-- title -->
		<div class="title">
			<h2>Login</h2>
		</div>
>>>>>>> 36431ad1a486a6ce13fff63af934ca38b66892c3

		<!-- container -->
		<div class="container">
			<!<!-- box -->
			<div class="box">
				<!-- image -->
				<div class="image">
					<img src="${pageContext.request.contextPath}/assets/logo/text.png" />
				</div>

<<<<<<< HEAD
                    <form onsubmit="return validateForm()" action="Login" method="POST">
                        <div class="detail username">
                            <label for="username">Username / Email</label>
                            <div class="icon">
                                <i class="fa-solid fa-user"></i>
                                <input type="text" id="username" name="username" placeholder="Enter username / email" value="${username}" />
                            </div>
                            <span class="error-message">${usernameError}</span>
                        </div>

                        <div class="detail password">
                            <label for="password">Password</label>
                            <div class="icon">
                                <i class="fa-solid fa-key"></i>
                                <input type="password" id="password" name="password" placeholder="Enter password" />
                            </div>
                            <span class="error-message">${passwordError}</span>
                        </div>
=======
				<!-- form -->
				<div class="form">
					<h1 class="login-title">Login</h1>

					<form onsubmit="return validateForm()" action="Login" method="POST">
						<div class="detail username">
							<label for="username">Username / Email</label>
							<div class="icon">
								<i class="fa-solid fa-user"></i>
								<input type="text" id="username" name="username" placeholder="Enter username / email" value="${username}" />
							</div>
							<span class="error-message">${usernameError}</span>
						</div>

						<div class="detail password">
							<label for="password">Password</label>
							<div class="icon">
								<i class="fa-solid fa-key"></i>
								<input type="password" id="password" name="password" placeholder="Enter password" />
							</div>
							<span class="error-message">${passwordError}</span>
						</div>
>>>>>>> 36431ad1a486a6ce13fff63af934ca38b66892c3

						<div class="forgot-passwd">
							<a href="reset.jsp">Forgot password?</a>
						</div>

						<button type="submit" class="button">Login</button>
					</form>

<<<<<<< HEAD
                    <!-- go to sign up page -->
                    <div class="signup">Don't have an account? <a href="register.jsp">Register</a></div>
                </div>
            </div>
        </div>
        <script>
            window.addEventListener("DOMContentLoaded", () => {
            <%
                    String[] errorFields = { "username", "password"};
                    for (String field : errorFields) {
                      String error = (String) request.getAttribute(field + "Error");
                      if (error != null) {
            %>
                showError('<%= field %>', '<%= error %>');
            <%
                      }
                    }
            %>
            })

            window.addEventListener("DOMContentLoaded", () => {
            <%
                    // Check if the "loginSuccess" attribute is set from the server-side
                    String loginSuccess = (String) request.getAttribute("loginSuccess");
                    String username = (String) request.getAttribute("username");  // Get the username

                    if (loginSuccess != null && loginSuccess.equals("true")) {
            %>
                    // Display SweetAlert2 success message with the username
                    Swal.fire({
                        title: 'Login Successful!',
                        text: 'Welcome, <%= username %>! You have logged in successfully.',
                        icon: 'success',
                        confirmButtonText: 'OK'
                    }).then(() => {
                        // Redirect to the index page after the popup
                        window.location.href = 'index.jsp';  // Redirect to index.jsp
                    });
            <%
                    }
            %>
            });
        </script>
    </body>
    <footer><%@include file="components/footer.jsp" %></footer>
    <script src="${pageContext.request.contextPath}/scripts/login.js"></script>
=======
					<!-- go to sign up page -->
					<div class="signup">Don't have an account? <a href="register.jsp">Register</a></div>
				</div>
			</div>
		</div>
		<script>
			window.addEventListener("DOMContentLoaded", () => {
			  <%
			    String[] errorFields = { "username", "password"};
			    for (String field : errorFields) {
			      String error = (String) request.getAttribute(field + "Error");
			      if (error != null) {
			  %>
			      showError('<%= field %>', '<%= error %>');
			  <%
			      }
			    }
			  %>
			})
		</script>
	</body>
	<footer><%@include file="components/footer.jsp" %></footer>
	<script src="${pageContext.request.contextPath}/scripts/login.js"></script>
>>>>>>> 36431ad1a486a6ce13fff63af934ca38b66892c3
</html>
