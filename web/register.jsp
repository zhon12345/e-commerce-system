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
		<title>Register</title>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/register.css" />
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
		<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	</head>
	<header><%@include file="components/navbar.jsp" %></header>

	<body>
		<div class="title">
			<h2>Register</h2>
		</div>

		<div class="container">
			<div class="box">
				<div class="image">
					<img src="${pageContext.request.contextPath}/assets/logo/text.png" />
				</div>
				<div class="form">
					<h1 class="signup-title">Register</h1>
					<form onsubmit="" action="register" method="post">
						<input type="hidden" name="csrf_token" value="${csrf_token}" />
						<div class="input username">
							<label for="username">Username</label>
							<div class="icon">
								<i class="fa-solid fa-user"></i>
								<input type="text" id="username" name="username" placeholder="Enter username" value="${username}" />
							</div>
							<span class="error-message"> ${usernameError} </span>
						</div>

						<div class="input email">
							<label for="email">Email</label>
							<div class="icon">
								<i class="fa-solid fa-envelope"></i>
								<input type="text" id="email" name="email" placeholder="Enter email" value="${email}" />
							</div>
							<span class="error-message"> ${emailError} </span>
						</div>

						<div class="input password">
							<label for="password">Password</label>
							<div class="icon">
								<i class="fa-solid fa-key"></i>
								<input type="password" id="password" name="password" placeholder="Enter password" />
							</div>
							<span class="error-message"> ${passwordError} </span>
						</div>

						<div class="input confirmPassword">
							<label for="confirm-password">Confirm Password</label>
							<div class="icon">
								<i class="fa-solid fa-key"></i>
								<input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm password" />
							</div>
							<span class="error-message"> ${confirmPasswordError} </span>
						</div>

						<button type="submit" class="button">Sign Up</button>
					</form>

					<div class="back">Already have an account? <a href="login.jsp">Login</a></div>
				</div>
			</div>
		</div>
		<script>
			window.addEventListener("DOMContentLoaded", () => {
			  <%
			    String[] errorFields = {"username", "email", "password", "confirmPassword"};
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

			<% if (session.getAttribute("error") != null) { %>
				Swal.fire({
					icon: 'error',
					title: 'Error',
					text: '<%= session.getAttribute("error") %>',
					showConfirmButton: false,
					timer: 3000
				});
				<% session.removeAttribute("error"); %>
			<% } %>
		</script>
	</body>
	<footer><%@include file="components/footer.jsp" %></footer>
	<script src="${pageContext.request.contextPath}/scripts/register.js" type="module"></script>
</html>
