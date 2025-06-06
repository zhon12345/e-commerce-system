<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Navbar</title>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/navbar.css" />
	</head>

	<body>
		<div class="navbar">
			<a href="${pageContext.request.contextPath}/index">
				<img src="${pageContext.request.contextPath}/assets/logo/text.png" class="logo" />
			</a>

			<div class="in-navbar">
				<a href="${pageContext.request.contextPath}/index">Home</a>
				<a href="${pageContext.request.contextPath}/products">Shop</a>
				<a href="${pageContext.request.contextPath}/about.jsp">About Us</a>
				<a href="${pageContext.request.contextPath}/contact.jsp">Contact</a>

				<div class="search-container">
					<form action="${pageContext.request.contextPath}/products" method="get">
						<input type="text" class="search-input" name="search" placeholder="Search products..." value="${param.search}" />
						<i class="fa fa-search search-icon" id="searchIcon" onclick="toggleSearch()"></i>
						<i class="fa fa-times close-icon" id="closeIcon" onclick="closeSearch()"></i>
						<button type="submit" hidden></button>
					</form>
				</div>

				<% if (session.getAttribute("user") !=null) { %>
					<div class="icon-container">
						<i class="fa-solid fa-cart-shopping nav-icon" onclick="location.href='${pageContext.request.contextPath}/cart'"></i>
						<div class="user-dropdown">
							<i class="fas fa-user-circle nav-icon"></i>
							<div class="dropdown-content">
								<a href="${pageContext.request.contextPath}/user/profile">My Profile</a>
								<a href="${pageContext.request.contextPath}/user/history">History</a>
								<a href="${pageContext.request.contextPath}/logout">Logout</a>
							</div>
						</div>
					</div>
				<% } else { %>
					<a href="${pageContext.request.contextPath}/login.jsp" class="login_signup-btn">Login / Register</a>
				<% } %>
			</div>
		</div>
	</body>
	<script src="${pageContext.request.contextPath}/scripts/navbar.js"></script>
</html>
