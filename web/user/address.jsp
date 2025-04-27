<%@page import="java.util.List, Model.Addresses"%>

<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>My Address</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/address.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/sidebar.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/empty_status.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/popup.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/popup_form.css">
		<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
	</head>
	<header>
		<%@include file="../components/navbar.jsp" %>
	</header>

	<body>

		<!-- success message popup -->
		<% if (session.getAttribute("addSuccess") != null || session.getAttribute("editSuccess") != null) { %>
		<div class="overlay show" id="overlay"></div>
		<div class="popup show" id="popup">
			<div class="success-icon">
				<i class="fas fa-check-circle"></i>
			</div>
			<div class="success-title"><%= session.getAttribute("editSuccess") != null ? "Edit" : session.getAttribute("deleteSuccess") != null ? "Delete" : "Add" %> Successful!</div>
			<button class="close" onclick="closePopup()">OK</button>
			<%
							session.removeAttribute("addSuccess");
	session.removeAttribute("editSuccess");
	session.removeAttribute("deleteSuccess");
			%>
		</div>
		<% } %>

		<!-- title -->
		<div class="title">
			<h2>Address</h2>
		</div>

		<div class="container">
			<!-- sidebar  -->
			<div class="sidebar">
				<h3>My Account</h3>
				<ul>
					<li><a href="profile.jsp">Profile</a></li>
					<li><a href="address" class="active">Address</a></li>
					<li><a href="card">Bank & Card</a></li>
					<li><a href="history.jsp">History</a></li>
				</ul>
			</div>

			<!-- content -->
			<div class="content">
				<div class="header">
					<h2>My Address</h2>
					<button class="add" id="addAddressBtn">Add New Address</button>
				</div>

				<!-- address display sample -->
				<div class="list">
					<%
					List<Addresses> addresses = (List<Addresses>) request.getAttribute("addresses");
					if (addresses != null && !addresses.isEmpty()) {
									for (Addresses addr : addresses) {
													String fullAddress = addr.getAddress1();
													if (addr.getAddress2() != null && !addr.getAddress2().isEmpty()) {
																	fullAddress += ", " + addr.getAddress2();
													}
													fullAddress += ", " + addr.getCity() + ", " + addr.getPostalCode() + ", " + addr.getState();
					%>
					<div class="card">
						<div class="details">
							<span><%= addr.getReceiverName() %></span>
							<div class="separator"></div>
							<span><%= addr.getContactNumber() %></span>
						</div>
						<div class="address"><%= fullAddress %></div>

						<div class="actions">
							<button class="edit-btn" onclick="window.location.href = '${pageContext.request.contextPath}/user/address?action=edit&id=<%= addr.getId() %>'">Edit</button>
							<button class="delete" onclick="confirmDelete('<%= request.getContextPath() + "/user/address?action=delete&id=" + addr.getId()%>')">Delete</button>
						</div>
					</div>
					<%
									}
					} else {
					%>
					<div class="empty-status">
						<i class="fas fa-map-marker-alt"></i>
						<h3>No Saved Addresses</h3>
						<p>You haven't added any addresses yet. Add your first address to get started.</p>
					</div>
					<% } %>
				</div>

			</div>
		</div>

					<!-- edit profile -->
					<div class="add-container" id="addPopup">
						<div class="add-content">
							<span class="close-btn" id="closePopupBtn">&times;</span>
							<h2><%= request.getAttribute("editAddress") != null ? "Edit Address" :  "Add New Address" %></h2>
							<form onsubmit="return validateForm()" action="${pageContext.request.contextPath}/user/address" method="POST">
								<% if (request.getAttribute("editAddress") != null) {
												Addresses editAddress = (Addresses) request.getAttribute("editAddress");
								%>
								<input type="hidden" name="action" value="edit">
								<input type="hidden" name="id" value="<%= editAddress.getId() %>">
								<% } %>

					<div class="add-info name">
						<label for="name">Receiver Name</label>
						<input type="text" id="name" name="name" value="${name}">
						<span class="error-message">${nameError}</span>
					</div>
					<div class="add-info phone">
						<label for="phone">Contact Number</label>
						<input type="tel" id="phone" name="phone" value="${phone}">
						<span class="error-message">${phoneError}</span>
					</div>
					<div class="add-info line1">
						<label for="line1">Address Line 1</label>
						<input type="text" id="line1" name="line1" value="${line1}">
						<span class="error-message">${line1Error}</span>
					</div>
					<div class="add-info">
						<label for="line2">Address Line 2 (Optional)</label>
						<input type="text" id="line2" name="line2" value="${line2}">
					</div>
					<div class="add-info postcode">
						<label for="postcode">Postal Code</label>
						<input type="text" id="postcode" name="postcode" value="${postcode}">
						<span class="error-message">${postcodeError}</span>
					</div>
					<div class="add-info city">
						<label for="city">City</label>
						<input type="text" id="city" name="city" value="${city}">
						<span class="error-message">${cityError}</span>
					</div>
					<div class="add-info state">
						<label for="state">State</label>
						<input type="text" id="state" name="state" value="${state}">
						<span class="error-message">${stateError}</span>
					</div>
					<button type="submit" class="btn"><%= request.getAttribute("editAddress") != null ? "Save" : "Add" %></button>
				</form>
			</div>
		</div>

					<script>
			window.addEventListener("DOMContentLoaded", () => {
						<%
																	String[] errorFields = {"name", "phone", "line1", "postcode", "city","state"};
																	for (String field : errorFields) {
																					String error = (String) request.getAttribute(field + "Error");
																					if (error != null) {
						%>
				addPopup.style.display = 'flex';
				showError('<%= field %>', '<%= error %>');
						<%
}
}

if (request.getAttribute("editAddress") != null) {
			%>
				addPopup.style.display = 'flex';
			<%
}
			%>

			});

			function confirmDelete(url) {
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
						window.location.href = url;
					}
				});
			}

			<% if (session.getAttribute("deleteSuccess") != null) { %>
			Swal.fire({
				icon: 'success',
				title: 'Deleted!',
				text: 'Your address has been deleted.',
				showConfirmButton: false,
				timer: 1500
			});
			<% session.removeAttribute("deleteSuccess"); %>
			<% } %>
		</script>
		<script src="${pageContext.request.contextPath}/scripts/components/popup.js"></script>
	</body>
	<footer>
		<%@include file="../components/footer.jsp" %>
	</footer>
	<script src="${pageContext.request.contextPath}/scripts/user/address.js" type="module"></script>

</html>