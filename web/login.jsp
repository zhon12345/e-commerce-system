<%--
    Document   : login
    Created on : 1 Apr 2025, 3:19:40 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Login Page</title>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/popup.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/login.css" />
	</head>
	<header><%@include file="components/navbar.jsp" %></header>

	<body>
		<% if (session.getAttribute("registerSuccess") !=null && session.getAttribute("registerSuccess").equals("true")) { %>
      <div class="overlay show" id="overlay"></div>
      <div class="popup show" id="popup">
        <div class="success-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="success-title">Register Successful!</div>
        <button class="close" onclick="closePopup()">OK</button>
        <% session.removeAttribute("registerSuccess"); %>
      </div>
		<% } %>

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

				<!-- form -->
				<div class="form">
					<h1 class="login-title">Login</h1>

					<form onsubmit="return validateForm()" action="login" method="POST">
						<input type="hidden" name="redirect" value="${param.redirect}" />

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

						<div class="forgot-passwd">
							<a href="reset.jsp">Forgot password?</a>
						</div>

						<button type="submit" class="button">Login</button>
					</form>

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
		<script src="${pageContext.request.contextPath}/scripts/components/popup.js"></script>
	</body>
	<footer><%@include file="components/footer.jsp" %></footer>
	<script src="${pageContext.request.contextPath}/scripts/login.js" type="module"></script>
</html>
