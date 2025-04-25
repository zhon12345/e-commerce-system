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
			<a href="${pageContext.request.contextPath}/index.jsp">
				<img src="${pageContext.request.contextPath}/assets/logo/text.png" class="logo" />
			</a>

			<div class="in-navbar">
				<a href="${pageContext.request.contextPath}/index.jsp">Home</a>
                                <a href="${pageContext.request.contextPath}/products">Shop</a>
                                <a href="${pageContext.request.contextPath}/about.jsp">About Us</a>
				<a href="${pageContext.request.contextPath}/contact.jsp">Contact</a>

				<div class="search-container">
					<input type="text" class="search-input" placeholder="Search products..." />
					<i class="fa fa-search search-icon" id="searchIcon" onclick="toggleSearch()"></i>
					<i class="fa fa-times close-icon" id="closeIcon" onclick="closeSearch()"></i>
				</div>

				<% if (session.getAttribute("user") !=null) { %>
					<div class="icon-container">
						<i class="fa-solid fa-cart-shopping nav-icon" onclick="location.href='${pageContext.request.contextPath}/cart.jsp'"></i>
						<div class="user-dropdown">
							<i class="fas fa-user-circle nav-icon"></i>
							<div class="dropdown-content">
								<a href="${pageContext.request.contextPath}/user/profile.jsp">My Profile</a>
								<a href="${pageContext.request.contextPath}/user/history.jsp">History</a>
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
