<%--
    Document   : product
    Created on : 22 Apr 2025, 12:49:51 am
    Author     : yjee0
--%>

<%@ page import="java.util.List, java.text.SimpleDateFormat, Model.Reviews"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${product.name} - Product Details</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/product.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
</head>
<header>
	<%@include file="components/navbar.jsp" %>
</header>

<body>
	<jsp:useBean id="product" class="Model.Products" scope="request" />

	<div class="title">
		<h2>Product Details</h2>
	</div>

	<div class="container">
		<div class="product-container">
			<!-- Left Box (Product Image) -->
			<div class="left-box">
				<%
					String productName = product.getName();
					String imagePath = request.getContextPath() + "/assets/products/" + productName + "/1";
				%>

				<img src="<%= imagePath %>.png" onerror="this.onerror=null; this.src='<%= imagePath %>.jpg'" alt="Product Image" class="product-image">
			</div>

			<!-- Right Box (Product Details) -->
			<div class="right-box">
				<div class="product-header">${product.name}</div>
				<div class="product-price">RM ${product.price}</div>

				<div class="quantity">
					<span id="quantity-label">${product.stock} units remaining</span>

					<form action="${pageContext.request.contextPath}/cart" method="post">
						<input type="hidden" name="action" value="add">
						<input type="hidden" name="productId" value="${product.id}">

						<div class="quantity-controls">
							<button type="button" class="quantity-btn" onclick="updateQuantity(-1)">-</button>
							<input type="text" name="quantity" class="quantity-input" value="1" readonly>
							<button type="button" class="quantity-btn" onclick="updateQuantity(1)">+</button>
						</div>

						<button type="submit" class="add-to-cart">Add to cart</button>
					</form>
				</div>
			</div>
		</div>

		<!-- Bottom Box (Description/Review) -->
		<div class="bottom-box">
			<div class="tab-container">
				<div class="tab <%= "description".equals(request.getAttribute("activeTab")) ? "active" : "" %>" onclick="switchTab('description', event)">Description</div>
				<div class="tab <%= "reviews".equals(request.getAttribute("activeTab")) ? "active" : "" %>" onclick="switchTab('reviews', event)">Reviews</div>
			</div>

			<div id="description-content" class="content-section" style="display: <%= "description".equals(request.getAttribute("activeTab")) ? "block" : "none" %>;">
				<div class="product-description">
					<h3>Product Description</h3>
					<p>${product.description.isEmpty() ? "No description available." : product.description}</p>
				</div>
			</div>

			<div id="reviews-content" class="content-section" style="display: <%= "reviews".equals(request.getAttribute("activeTab")) ? "block" : "none" %>;">
				<div class="review-section">
					<h3>Your Review</h3>
					<form onsubmit="return validateForm()" action="${pageContext.request.contextPath}/user/review" method="post" id="reviewForm">
						<input type="hidden" name="productId" value="${product.id}">
						<input type="hidden" name="activeTab" value="reviews">
						<input type="hidden" name="action" value="add">

						<div class="rating">
							<div class="stars">
								<span class="rating-label" >Rating:</span>
								<div class="star-rating">
									<% for (int i=1; i<=5; i++) { %>
									<i class="fa-regular fa-star star" data-value="<%= i %>"></i>
									<% } %>
								</div>
							</div>
							<input type="hidden" name="rating" id="selectedRating" value="${selectedRating}">
							<span class="error-message" id="rating">${ratingError}</span>
						</div>

						<div class="review">
							<textarea class="review-textarea" name="reviewText" id="review" placeholder="Write your review here..." >${reviewText}</textarea>
							<span class="error-message">${reviewError}</span>
						</div>
						<button type="submit" class="submit-review">Submit Review</button>
					</form>
				</div>

				<div class="existing-reviews">
					<h3>Customer Reviews</h3>
					<div class="reviews-container">
						<%
							List<Reviews> reviewList = (List<Reviews>) request.getAttribute("reviewList");
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

							if (reviewList == null || reviewList.isEmpty()) {
						%>
						<p class="no-reviews">No reviews yet. Be the first to review this product!</p>
						<%
							} else {
								for (Reviews review : reviewList) {
						%>
						<div class="review-item">
							<div class="review-header">
								<div class="right">
									<span class="reviewer-name"><%= review.getUserId().getUsername() %></span>
									<span class="review-date">Posted on: <%= dateFormat.format(review.getReviewDate()) %></span>
								</div>

								<div class="review-stars">
								<% for(int i=0; i<5; i++) { %>
									<i class="fa-star <%= i < review.getRating() ? "fa-solid" : "fa-regular" %>"></i>
								<% } %>
								</div>
							</div>
							<div class="review-content">
								<%= review.getReview().replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("\n", "<br>")  %>
							</div>
						</div>
						<%
								}
							}
						%>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script>
		window.addEventListener("DOMContentLoaded", () => {
		<%
			String[] errorFields = { "rating", "review",};
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
	<script src="${pageContext.request.contextPath}/scripts/product.js" type="module"></script>
</body>
<footer>
	<%@include file="components/footer.jsp" %>
</footer>

</html>