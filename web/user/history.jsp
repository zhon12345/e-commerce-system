<%--
    Document   : history
    Created on : 15 Apr 2025, 11:01:40 pm
    Author     : yjee0
--%>

<%@ page import="java.util.List, java.util.Map, java.text.SimpleDateFormat, Model.Orders, Model.Orderdetails, Model.Products"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Order History</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/empty_status.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/sidebar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/history.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<header>
	<%@include file="../components/navbar.jsp" %>
</header>

<body>
	<!-- title -->
	<div class="title">
		<h2>Order History</h2>
	</div>

	<div class="container">
		<!-- Sidebar Navigation -->
		<jsp:include page="/components/sidebar.jsp">
			<jsp:param name="activePage" value="history"/>
    </jsp:include>

		<!-- content -->
		<div class="content">
			<div class="header-status">
				<h2>My Orders</h2>
			</div>

			<%
				List<Orders> orderList = (List<Orders>) request.getAttribute("orderList");
				Map<Integer, List<Orderdetails>> orderDetailsMap = (Map<Integer, List<Orderdetails>>) request.getAttribute("orderDetailsMap");
				if (orderList == null || orderList.isEmpty()) {
			%>
			<div class="empty-status">
				<h3>No orders yet</h3>
				<p>You haven't placed any orders. Start shopping now!</p>
				<a href="${pageContext.request.contextPath}/products">
					<button class="btn btn-primary" style="margin-top: 15px;">Shop Now</button>
				</a>
			</div>
			<%
				} else {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
					for (Orders order : orderList) {
						List<Orderdetails> detailsList = orderDetailsMap.get(order.getId());
			%>
			<div class="card">
				<div class="header">
					<div>
						<span class="id">Order #<%= order.getId() %></span>
						<span class="date"> • <%= dateFormat.format(order.getOrderDate()) %></span>
					</div>
					<span class="status status-completed">Completed</span>
				</div>

				<%
					int totalItems = 0;
					for (Orderdetails detail : detailsList) {
						Products product = detail.getProductId();
						totalItems += detail.getQuantity();
				%>
				<div class="details">
					<a href="${pageContext.request.contextPath}/product?id=<%= product.getId() %>">
						<%
							String imagePath = request.getContextPath() + "/assets/products/" + product.getName() + "/1";
						%>

						<img src="<%= imagePath %>.png" onerror="this.onerror=null; this.src='<%= imagePath %>.jpg'" alt="<%= product.getName() %>" class="image">
					</a>
					<div class="info">
						<div class="name"><%= product.getName() %></div>
						<div class="subtext">
							<div class="category"><%= product.getCategoryId().getName() %></div>
							<div class="quantity">x<%= detail.getQuantity() %></div>
						</div>
						<div class="price">RM <%= String.format("%.2f", detail.getPrice().doubleValue()) %></div>
					</div>
				</div>
				<% } %>

				<div class="summary-line">
					<span>Total <%= totalItems > 1 ? totalItems + " items" : totalItems + " item" %>:</span>
					<span>RM <%= String.format("%.2f", order.getTotalPrice()) %></span>
				</div>
			</div>
			<%
					}
				}
			%>
		</div>
	</div>

	<script>
		<% if (session.getAttribute("orderSuccess") != null) { %>
			Swal.fire({
				icon: 'success',
				title: 'Success!',
				text: 'Your order has been completed successfully.',
				showConfirmButton: false,
				timer: 1500
			});
			<% session.removeAttribute("orderSuccess"); %>
		<% } %>
	</script>
</body>
<footer>
	<%@include file="../components/footer.jsp" %>
</footer>

</html>