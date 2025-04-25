<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>My Profile</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/profile.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/sidebar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/popup.css">
</head>
<header>
	<%@include file="../components/navbar.jsp" %>
</header>

<body>
	<jsp:useBean id="user" class="Model.Users" scope="session" />

	<!-- success message popup -->
	<% if (session.getAttribute("editSuccess") !=null && session.getAttribute("editSuccess").equals("true")) { %>
		<div class="overlay show" id="overlay"></div>
		<div class="popup show" id="popup">
			<div class="success-icon">
				<i class="fas fa-check-circle"></i>
			</div>
			<div class="success-title">Edit Successful!</div>
			<button class="close" onclick="closePopup()">OK</button>
			<% session.removeAttribute("editSuccess"); %>
		</div>
	<% } %>

	<!-- title -->
	<div class="title">
		<h2>Profile</h2>
	</div>

	<div class="container">
		<!-- sidebar -->
		<div class="sidebar">
			<h3>My Account</h3>
			<ul>
				<li><a href="profile.jsp" class="active">Profile</a></li>
				<li><a href="address">Address</a></li>
				<li><a href="bank.jsp">Bank & Card</a></li>
				<li><a href="history.jsp">History</a></li>
			</ul>
		</div>

		<!-- content of avatar -->
		<div class="content">
			<h2>Personal Information</h2>

			<form onsubmit="return validateForm()" action="${pageContext.request.contextPath}/user/profile" method="post" enctype="multipart/form-data">
				<div class="form-content">
					<div class="left">
						<div class="info">
							<label for="username">Username:</label>
							<input type="text" id="username" name="username" value="${user.username}" disabled>
						</div>

						<div class="info name">
							<label for="name">Name:</label>
							<input type="text" id="name" name="name" value="${name != null ? name : user.name}">
							<span class="error-message"> ${nameError} </span>
						</div>

						<div class="info email">
							<label for="email">Email:</label>
							<input type="email" id="email" name="email" value="${email != null ? email : user.email}">
							<span class="error-message"> ${emailError} </span>
						</div>

						<div class="info phone">
							<label for="phone">Phone:</label>
							<input type="tel" id="phone" name="phone" value="${phone != null ? phone : user.contact}">
							<span class="error-message"> ${contactError} </span>
						</div>
					</div>

					<div class="right">
						<div class="avatar">
							<img src="<%= user.getAvatar() != null ? request.getContextPath() + "/uploads/" + user.getAvatar() : request.getContextPath() + "/assets/avatars/default.webp" %>" alt="Avatar" class="pic" id="userAvatar">
							<!-- upload pic -->
							<div class="upload-pic" title="Change photo">
								<i class="fas fa-camera"></i>
								<input type="file" id="avatar" name="avatar" accept="image/*" style="display: none;">
							</div>
							<span class="error-message"> ${avatarError} </span>
						</div>
					</div>
				</div>

				<div class="form-actions">
					<button type="submit" class="save">Save Changes</button>
				</div>
			</form>
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
	</script>
	<script src="${pageContext.request.contextPath}/scripts/components/popup.js"></script>
</body>
<footer>
	<%@include file="../components/footer.jsp" %>
</footer>
<script src="${pageContext.request.contextPath}/scripts/user/profile.js" type="module"></script>

</html>