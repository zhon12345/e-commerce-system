<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
	if (session.getAttribute("user") != null) {
		response.sendRedirect(request.getContextPath() + "/index");
		return;
	}
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Login</title>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/login.css" />
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
		<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	</head>
	<header><%@include file="components/navbar.jsp" %></header>

	<body>
		<div class="title">
			<h2>Login</h2>
		</div>

		<div class="container">
			<div class="box">
				<div class="image">
					<img src="${pageContext.request.contextPath}/assets/logo/text.png" />
				</div>

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

			<% if (session.getAttribute("success") != null) { %>
				Swal.fire({
					icon: 'success',
					title: '<%= session.getAttribute("success") %>',
					timer: 3000
				});
				<% session.removeAttribute("success"); %>
			<% } %>
		</script>
	</body>
	<footer><%@include file="components/footer.jsp" %></footer>
	<script src="${pageContext.request.contextPath}/scripts/login.js" type="module"></script>
</html>