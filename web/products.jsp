<%@ page import="java.util.List, java.util.Map, Model.Products, Model.Categories"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Shop Page</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/empty_status.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/products.css">
</head>
<header>
	<%@ include file="components/navbar.jsp" %>
</header>

<body>
	<div class="title">
		<h2>All Products</h2>
	</div>

	<div class="container">
		<form class="box" method="GET" action="${pageContext.request.contextPath}/products">
			<div class="filter">
				<h3>Product Category</h3>
				<% List<Categories> categories = (List<Categories>) request.getAttribute("categories");
					if (categories != null && !categories.isEmpty()) {
						for (Categories category : categories) {
							String[] selectedCategories = request.getParameterValues("category");
							boolean isChecked = false;
							if (selectedCategories != null) {
								for (String selectedCategory : selectedCategories) {
									if (selectedCategory.equals(category.getName())) {
										isChecked = true;
										break;
									}
								}
							}
				%>
						<div class="option">
							<input type="checkbox" name="category" value="<%= category.getName() %>" id="cat<%= category.getId() %>" <%= isChecked ? "checked" : "" %> onchange="this.form.requestSubmit()">
							<label for="cat<%= category.getId() %>"><%= category.getName() %></label>
						</div>
				<%
						}
					}
				%>
			</div>

			<div class="filter">
				<h3>Price Range</h3>
				<div class="range">
					<input type="number" name="min" id="min" placeholder="Min" min="0" value="${param.min}" onchange="this.form.requestSubmit()">
					<span>to</span>
					<input type="number" name="max" id="max" placeholder="Max" min="0" value="${param.max}" onchange="this.form.requestSubmit()">
				</div>
			</div>

			<div class="filter">
				<h3>Rating</h3>
				<div class="star-rating" id="starContainer">
					<% for (int i=1; i<=5; i++) { %>
						<i class="fa-regular fa-star star" data-value="<%= i %>"></i>
					<% } %>
				</div>
				<div class="p-rating">Click stars to filter</div>
				<input type="hidden" name="rating" id="selectedRating" value="${param.rating}">
			</div>

			<button type="button" id="reset">Reset</button>
		</form>

		<div class="products-section">
			<% List<Products> products = (List<Products>) request.getAttribute("products");
				if (products != null && !products.isEmpty()) {
			%>
			<div class="products-grid">
				<% for (Products product : products) { %>
				<div class="card">
					<a href="${pageContext.request.contextPath}/product?id=<%= product.getId() %>" class="link">
						<div class="image">
							<img src="${pageContext.request.contextPath}/<%= product.getImagePath() %>" alt="<%= product.getName() %>">
						</div>
						<div class="name"><%= product.getName() %></div>
						<div class="price">RM <%= product.getPrice() %></div>
						<div class="product-rating">
							<%
								Map<Integer, Double> averageRatings = (Map<Integer, Double>) request.getAttribute("averageRatings");
								Double avgRating = null;

								if (averageRatings != null) avgRating = averageRatings.get(product.getId());
								if (avgRating == null) avgRating = 0.0;

								for (int i = 0; i < 5; i++) {
									double starLower = i + 0.5;
									double starUpper = i + 1.0;
									String iconClass;

									if (avgRating >= starUpper) {
										iconClass = "fa-solid fa-star";
									} else if (avgRating >= starLower) {
										iconClass = "fa-regular fa-star-half-stroke";
									} else {
										iconClass = "fa-regular fa-star";
									}
							%>
								<i class="<%= iconClass %>"></i>
							<% } %>
						</div>
					</a>
					<form action="${pageContext.request.contextPath}/cart" method="post">
						<input type="hidden" name="action" value="add">
						<input type="hidden" name="productId" value="<%= product.getId() %>">
						<button class="add-to-cart">Add to Cart</button>
					</form>
				</div>
				<% } %>
			</div>
			<%
				} else {
			%>
			<div class="empty-status">
				<i class="fa-solid fa-filter-circle-xmark"></i>
				<h3>No results...</h3>
				<p>No results found, please try again.</p>
				<a href="${pageContext.request.contextPath}/products">
					<button class="btn btn-primary">Reset</button>
				</a>
			</div>
			<% } %>
		</div>
	</div>
	<script>
		document.getElementById("reset").addEventListener("click", () => {
			window.location.href = "${pageContext.request.contextPath}/products";
		});
	</script>
</body>
<footer>
	<%@ include file="components/footer.jsp" %>
</footer>
<script src="${pageContext.request.contextPath}/scripts/products.js"></script>

</html>