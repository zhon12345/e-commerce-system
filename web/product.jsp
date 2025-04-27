<%--
    Document   : product
    Created on : 22 Apr 2025, 12:49:51 am
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${product.name} - Product Details</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/product.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
	<script>
		function switchTab(tabName, event) {
			// Hide all content sections
			document.querySelectorAll('.content-section').forEach(section => {
				section.style.display = 'none';
			});

			// Remove active class from all tabs
			document.querySelectorAll('.tab').forEach(tab => {
				tab.classList.remove('active');
			});

			// Show the selected content section
			document.getElementById(tabName + '-content').style.display = 'block';

			// Add active class to the clicked tab
			event.currentTarget.classList.add('active');
		}

		function updateQuantity(change) {
			const input = document.querySelector('.quantity-input');
			let value = parseInt(input.value) + change;
			const maxStock = parseInt('${product.stock}');

			// Ensure quantity is between 1 and maximum available
			if (value < 1) value = 1;
			if (value > maxStock) value = maxStock;

			input.value = value;
		}
	</script>
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
				<img src="${pageContext.request.contextPath}/assets/products/${product.name}/1.png" alt="Product Image" class="product-image">
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
				<div class="tab active" onclick="switchTab('description', event)">Description</div>
				<div class="tab" onclick="switchTab('reviews', event)">Reviews</div>
			</div>

			<div id="description-content" class="content-section" style="display: block;">
				<div class="product-description">
					<h3>Product Description</h3>
					<p>${product.description.isEmpty() ? "No description available." : product.description}</p>
				</div>
			</div>

				<div id="reviews-content" class="content-section" style="display: none;">
					<div class="review-section">
						<h3 class="review-title">Your Review</h3>

						<!-- Star Rating -->
						<div class="rating-container">
							<div class="rating-stars">
								<i class="fa-regular fa-star" data-rating="1"></i>
								<i class="fa-regular fa-star" data-rating="2"></i>
								<i class="fa-regular fa-star" data-rating="3"></i>
								<i class="fa-regular fa-star" data-rating="4"></i>
								<i class="fa-regular fa-star" data-rating="5"></i>
							</div>
							<input type="hidden" id="selected-rating" value="0">
						</div>

						<div class="review-input-container">
							<textarea
								class="review-textarea"
								placeholder="Your Review...."
								rows="4"
								></textarea>
							<button class="submit-review">Submit Review</button>
						</div>
					</div>

					<div class="existing-reviews">
						<h3>Customer Reviews</h3>

						<!-- Example Review Item -->
						<div class="review-item">
							<div class="review-header">
                <div>
									<span class="review-author">John Doe</span>
									<span class="review-meta">- May 15, 2024</span>
                </div>
                <div class="review-rating">
									<i class="fa-solid fa-star"></i>
									<i class="fa-solid fa-star"></i>
									<i class="fa-solid fa-star"></i>
									<i class="fa-regular fa-star"></i>
									<i class="fa-regular fa-star"></i>
                </div>
							</div>

							<div class="review-content">
                <div class="review-detail">
									<p>Nice Good</p>
                </div>
							</div>
						</div>

						<!-- No Reviews Fallback -->
						<p class="no-reviews">No reviews yet. Be the first to review this product!</p>
					</div>
				</div>
		</div>
	</div>
</body>
<footer>
	<%@include file="components/footer.jsp" %>
</footer>

</html>