<%@page import="java.util.List, Model.Addresses"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>My Address</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/sidebar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/empty_status.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/popup_form.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/address.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
</head>
<header>
	<%@include file="../components/navbar.jsp" %>
</header>

<body>
	<div class="title">
		<h2>Address</h2>
	</div>

	<div class="container">
		<jsp:include page="/components/sidebar.jsp">
			<jsp:param name="activePage" value="address"/>
    </jsp:include>

		<div class="content">
			<div class="header">
				<h2>My Address</h2>
				<button class="add" id="addAddressBtn">Add New Address</button>
			</div>

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
						<span>
							<%= addr.getReceiverName() %>
						</span>
						<div class="separator"></div>
						<span>
							<%= addr.getContactNumber() %>
						</span>
					</div>
					<div class="address">
						<%= fullAddress %>
					</div>

					<div class="actions">
						<button class="edit-btn" onclick="window.location.href = '${pageContext.request.contextPath}/user/address?action=update&id=<%= addr.getId() %>'">Edit</button>
						<button class="delete" onclick="confirmDelete(<%= addr.getId()%>)">Delete</button>
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

	<div class="add-container" id="addPopup">
		<div class="add-content">
			<span class="close-btn" id="closePopupBtn">&times;</span>
			<h2>
				<%= request.getAttribute("updateAddress") !=null ? "Edit Address" : "Add New Address" %>
			</h2>
			<form onsubmit="return validateForm()" action="${pageContext.request.contextPath}/user/address" method="POST">
				<%
					if (request.getAttribute("updateAddress") !=null) {
						Addresses updateAddress = (Addresses) request.getAttribute("updateAddress");
				%>
				<input type="hidden" name="id" value="<%= updateAddress.getId() %>">
				<% } %>
				<input type="hidden" name="action" value="<%= request.getAttribute("updateAddress") !=null ? "update" : "create" %>">

				<div class="add-info name">
					<label for="name">Receiver Name</label>
					<input type="text" id="name" name="name" value="${name}">
					<span class="error-message">${nameError}</span>
				</div>
				<div class="add-info phone">
					<label for="phone">Contact Number</label>
					<input type="tel" id="phone" name="phone" value="${phone}" maxlength="11">
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
					<input type="text" id="postcode" name="postcode" value="${postcode}" maxlength="5">
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
				<button type="submit" class="btn">
					<%= request.getAttribute("updateAddress") !=null ? "Save" : "Add" %>
				</button>
			</form>
		</div>
	</div>

	<form id="deleteForm" method="POST" action="${pageContext.request.contextPath}/user/address" style="display:none;">
		<input type="hidden" name="action" value="delete">
		<input type="hidden" id="deleteId" name="id">
	</form>

	<script>
		window.addEventListener("DOMContentLoaded", () => {
			const addPopup = document.getElementById('addPopup');
			<%
				String[] errorFields = { "name", "phone", "line1", "postcode", "city", "state"};
				for (String field : errorFields) {
					String error = (String) request.getAttribute(field + "Error");
					if (error != null) {
			%>
			addPopup.style.display = 'flex';
			showError('<%= field %>', '<%= error %>');
			<%
					}
				}

				if (request.getAttribute("updateAddress") != null) {
			%>
				addPopup.style.display = 'flex';
			<% } %>
			});

		function confirmDelete(id) {
			Swal.fire({
				title: 'Are you sure?',
				text: "You won't be able to revert this!",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#d33',
				cancelButtonColor: '#4C60DF',
				confirmButtonText: 'Yes, delete it!'
			}).then((result) => {
				if (result.isConfirmed) {
					document.getElementById('deleteId').value = id;
					document.getElementById('deleteForm').submit();
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
	</script>
</body>
<footer>
	<%@include file="../components/footer.jsp" %>
</footer>
<script src="${pageContext.request.contextPath}/scripts/user/address.js" type="module"></script>

</html>