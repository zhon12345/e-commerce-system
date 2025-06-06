<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>My Profile</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/sidebar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/profile.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<header>
	<%@include file="../components/navbar.jsp" %>
</header>

<body>
	<jsp:useBean id="user" class="Model.Users" scope="session" />

	<div class="title">
		<h2>Profile</h2>
	</div>

	<div class="container">
		<jsp:include page="/components/sidebar.jsp">
			<jsp:param name="activePage" value="profile"/>
    </jsp:include>

		<div class="content">
			<h2>Personal Information</h2>

			<form onsubmit="return validateForm()" action="${pageContext.request.contextPath}/user/profile" method="post" enctype="multipart/form-data">
				<div class="form-content">
					<div class="left">
						<div class="info">
							<label for="username">Username:</label>
							<input type="text" id="username" name="username" value="${user.username}" readonly>
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
							<img src="${pageContext.request.contextPath}/<%= user.getAvatarPath() != null ? "uploads/" + user.getAvatarPath() : "/assets/avatars/default.webp" %>" alt="Avatar" class="pic" id="userAvatar">
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

		<% if (session.getAttribute("success") != null) { %>
			Swal.fire({
				icon: 'success',
				title: 'Success',
				text: '<%= session.getAttribute("success") %>',
				confirmButtonColor: '#4C60DF',
				timer: 1500
			});
			<% session.removeAttribute("success"); %>
		<% } %>
	</script>
</body>
<footer>
	<%@include file="../components/footer.jsp" %>
</footer>
<script src="${pageContext.request.contextPath}/scripts/user/profile.js" type="module"></script>

</html>