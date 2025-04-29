<%--
Document   : index
    Created on : 31 Mar 2025, 3:08:46 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

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
	<header><%@include file="components/navbar.jsp" %></header>

	<body>
		<img src="assets/home/hero.jpg" class="web_img" />

		<!-- Voucher Section -->
		<div class="voucher-section">
			<h2 class="section-title">Available Vouchers</h2>
			<div class="voucher-grid">
        <!-- Voucher 1 -->
        <div class="voucher-item">
					<div class="voucher-header">
						<i class="fas fa-ticket-alt voucher-icon"></i>
						<h3 class="voucher-title">15% OFF on all products</h3>
					</div>
					<p class="voucher-desc">Use code:</p>
					<div class="voucher-code">SPRING15</div>
					<div class="voucher-expiry">Valid until 30 June 2025</div>
					<button class="redeem-btn">Redeem Now</button>
        </div>

        <!-- Voucher 2 -->
        <div class="voucher-item">
					<div class="voucher-header">
						<i class="fas fa-ticket-alt voucher-icon"></i>
						<h3 class="voucher-title">Free Shipping Nationwide</h3>
					</div>
					<p class="voucher-desc">Use code:</p>
					<div class="voucher-code">FREESHIP</div>
					<div class="voucher-expiry">Valid until 31 May 2025</div>
					<button class="redeem-btn">Redeem Now</button>
        </div>

        <!-- Voucher 3 -->
        <div class="voucher-item">
					<div class="voucher-header">
						<i class="fas fa-ticket-alt voucher-icon"></i>
						<h3 class="voucher-title">20% OFF for new users</h3>
					</div>
					<p class="voucher-desc">Use code:</p>
					<div class="voucher-code">NEWUSER20</div>
					<div class="voucher-expiry">Valid until 31 Dec 2025</div>
					<button class="redeem-btn">Redeem Now</button>
        </div>
			</div>
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
	<footer><%@include file="components/footer.jsp" %></footer>
</html>