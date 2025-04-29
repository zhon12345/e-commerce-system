<%--
		Document   : index
    Created on : 31 Mar 2025, 3:08:46 pm
    Author     : yjee0
--%>
<%@page import="java.util.List, java.text.SimpleDateFormat, Model.Promotions" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Home Page</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/index.css" />
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<header>
	<%@include file="components/navbar.jsp" %>
</header>

<body>
	<img src="assets/home/hero.jpg" class="web_img" />

	<!-- Voucher Section -->
	<div class="voucher-section">
		<h2 class="section-title">Available Vouchers</h2>
		<%
			List<Promotions> promotions = (List<Promotions>) request.getAttribute("promotions");
			if (promotions != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		%>
		<div class="voucher-grid">
			<% for (Promotions promo : promotions) { %>
			<div class="voucher-item">
				<i class="fas fa-ticket-alt voucher-icon"></i>
				<div class="voucher-details">
					<p class="voucher-desc">Use code:</p>
					<div class="voucher-code"><%= promo.getPromoCode() %></div>
					<div class="voucher-discount">(<%= (int) (promo.getDiscount().doubleValue() * 100) %>% OFF)</div>
					<div class="voucher-expiry">Valid until <%= dateFormat.format(promo.getValidTo()) %></div>
				</div>
			</div>
			<% } %>
		</div>
		<% } else {%>
		<% } %>
	</div>

	<!--Categories-->
	<div class="category-section">
		<h2 class="category-title">Category</h2>

		<div class="categories">
			<a href="${pageContext.request.contextPath}/products?category=IEM" class="category-item">
				<img src="assets/home/iem.jpg" />
				<span class="category-name">IEM</span>
			</a>

			<a href="${pageContext.request.contextPath}/products?category=Mouse" class="category-item">
				<img src="assets/home/mouse.png" />
				<span class="category-name">Mouse</span>
			</a>

			<a href="${pageContext.request.contextPath}/products?category=Keyboard" class="category-item">
				<img src="assets/home/keyboard.png" />
				<span class="category-name">Keyboard</span>
			</a>
		</div>
	</div>

	<script>
	<% if (session.getAttribute("loginSuccess") != null && session.getAttribute("loginSuccess").equals("true")) { %>
		Swal.fire({
			icon: 'success',
			title: 'Login Successful!',
			showConfirmButton: true,
			timer: 3000
		});
		<% session.removeAttribute("loginSuccess"); %>
	<% } %>

	<% if (session.getAttribute("logoutSuccess") != null && session.getAttribute("logoutSuccess").equals("true")) { %>
		Swal.fire({
			icon: 'success',
			title: 'Logout Successful!',
			showConfirmButton: true,
			timer: 3000
		});
		<% session.removeAttribute("logoutSuccess"); %>
	<% } %>
	</script>
</body>
<footer>
	<%@include file="components/footer.jsp" %>
</footer>

</html>