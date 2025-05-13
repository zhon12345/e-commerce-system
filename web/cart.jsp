<%@ page import="java.util.List, Model.Products, Model.Cart"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Shopping Cart</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/cart.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<header>
	<%@include file="components/navbar.jsp" %>
</header>

<body>
	<div class="title">
		<h2>Shopping Cart</h2>
	</div>

	<div class="container">
		<div class="items">
			<%
				List<Cart> cart = (List<Cart>) request.getAttribute("cartList");
				if (cart != null && !cart.isEmpty()) {
			%>
			<div class="items-container">
				<div class="header">
					<div class="cart-title">Your Cart</div>
					<div class="item-count">${totalItems} items</div>
				</div>

				<%
				for (Cart item : cart) {
					Products product = item.getProductId();
				%>
				<div class="item">
					<a href="${pageContext.request.contextPath}/product?id=<%= product.getId() %>">
						<img src="${pageContext.request.contextPath}/<%= product.getImagePath() %>" alt="<%= product.getName() %>" class="item-image">
					</a>
					<div class="item-details">
						<div class="item-title"><%= product.getName() %></div>
						<div class="item-price">RM <%= product.getPrice() %></div>
						<div class="quantity">
							<form action="${pageContext.request.contextPath}/cart" method="post">
								<input type="hidden" name="productId" value="<%= product.getId() %>">
								<button type="submit" name="action" value="decrease" class="quantity-btn">-</button>
								<input type="number" value="<%= item.getQuantity() %>" class="quantity-input" readonly>
								<button type="submit" name="action" value="increase" class="quantity-btn">+</button>
							</form>
						</div>
					</div>
					<div>
						<form action="${pageContext.request.contextPath}/cart" method="post">
							<input type="hidden" name="productId" value="<%= product.getId() %>">
							<button type="submit" name="action" value="remove" class="remove-btn">
								<i class="fas fa-trash"></i>
							</button>
						</form>
					</div>
				</div>
				<% } %>
			</div>
			<% } else { %>
			<div class="items-container">
				<div class="empty">
					<i class="fas fa-shopping-cart"></i>
					<h3>Your cart is empty</h3>
					<p>Looks like you haven't added anything to your cart yet</p>
					<a href="${pageContext.request.contextPath}/products" class="empty-btn">Continue Shopping</a>
				</div>
			</div>
			<% } %>
		</div>

		<div class="summary">
			<h3 class="summary-title">Order Summary</h3>

			<div class="summary-detail">
				<span>Subtotal (${totalItems != null ? totalItems : 0} items)</span>
				<span>RM ${subtotal}</span>
			</div>
			<div class="summary-detail">
				<span>Shipping</span>
				<span>${shipping}</span>
			</div>
			<div class="summary-detail">
				<span>Tax (6%)</span>
				<span>RM ${tax}</span>
			</div>
			<div class="summary-total">
				<span>Total</span>
				<span>RM ${total}</span>
			</div>
			<a href="${pageContext.request.contextPath}/checkout">
				<button type="submit" class="checkout-btn" id="checkoutBtn">Proceed to Checkout</button>
			</a>
			<a href="${pageContext.request.contextPath}/products" class="back">Continue Shopping</a>
		</div>
	</div>

	<script>
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
<footer>
	<%@include file="components/footer.jsp" %>
</footer>

</html>