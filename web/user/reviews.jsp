<%@ page import="java.util.List, java.text.SimpleDateFormat, Model.Reviews" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>My Profile</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/sidebar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/empty_status.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/reviews.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<header>
	<%@include file="../components/navbar.jsp" %>
</header>

<body>
	<div class="title">
		<h2>Reviews</h2>
	</div>

	<div class="container">
		<jsp:include page="/components/sidebar.jsp">
			<jsp:param name="activePage" value="reviews"/>
    </jsp:include>

		<div class="content">
			<div class="header-status">
				<h2>My Reviews</h2>
			</div>

			<%
				List<Reviews> reviews = (List<Reviews>) request.getAttribute("reviews");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

				if (reviews == null || reviews.isEmpty()) {
			%>
			<div class="empty-status">
				<i class="fa-regular fa-comment-dots"></i>
				<h3>No comments yet</h3>
				<p>You haven't reviewed any products yet.</p>
				<a href="${pageContext.request.contextPath}/products">
					<button class="btn btn-primary">View Products</button>
				</a>
			</div>
			<% } else { %>
			<div class="review-list">
				<% for (Reviews review : reviews) { %>
				<div class="card">
					<div class="left">
						<div class="header">
							<a href="${pageContext.request.contextPath}/product?id=<%= review.getProductId() %>">
								<img src="${pageContext.request.contextPath}/<%= review.getProductId().getImagePath() %>" alt="<%= review.getProductId().getName() %>" class="image">
							</a>
							<div class="info">
								<div class="name"><%= review.getProductId().getName() %></div>
								<div class="review-stars">
									<% for(int i=0; i<5; i++) { %>
										<i class="fa-star <%= i < review.getRating() ? "fa-solid" : "fa-regular" %>"></i>
									<% } %>
									</div>
								<div class="date"><%= dateFormat.format(review.getReviewDate()) %></div>
							</div>
						</div>

						<div class="details">
							<%= review.getReview().replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("\n", "<br>")  %>
						</div>
					</div>

					<form onsubmit="return confirmDelete(event)" action="${pageContext.request.contextPath}/user/reviews" method="post" class="right">
						<input type="hidden" name="reviewId" value="<%= review.getId() %>">
						<button type="submit" class="remove-btn">
							<i class="fas fa-trash"></i>
						</button>
					</form>
				</div>
				<% } %>
			</div>
			<% } %>
		</div>
	</div>

	<script>
		function confirmDelete(e) {
			e.preventDefault();
			const form = e.target.closest('form');

			Swal.fire({
				title: 'Are you sure?',
				text: "You won't be able to revert this!",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#d33',
				cancelButtonColor: '#3085d6',
				confirmButtonText: 'Yes, delete it!'
			}).then((result) => {
				if (result.isConfirmed) {
					const actionInput = document.createElement('input');
					actionInput.type = 'hidden';
					actionInput.name = 'action';
					actionInput.value = 'delete';
					form.appendChild(actionInput);

					form.submit();
				}
			});
		}

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

		<% if (session.getAttribute("error") != null) { %>
			Swal.fire({
				icon: 'error',
				title: 'Error',
				text: 'Something went wrong, please try again.',
				showConfirmButton: false,
				timer: 1500
			});
			<% session.removeAttribute("error"); %>
		<% } %>
	</script>
</body>
<footer>
	<%@include file="../components/footer.jsp" %>
</footer>

</html>