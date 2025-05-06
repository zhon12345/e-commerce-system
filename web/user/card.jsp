<%@page import="java.util.List, Model.Cardinfo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Bank & Cards</title>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/sidebar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/empty_status.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/popup_form.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/card.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<header>
	<%@include file="../components/navbar.jsp" %>
</header>

<body>
	<div class="title">
		<h2>Bank & Cards</h2>
	</div>

	<div class="container">
		<jsp:include page="/components/sidebar.jsp">
			<jsp:param name="activePage" value="card" />
		</jsp:include>

		<div class="content">
			<div class="header">
				<h2>Credit/Debit Cards</h2>
				<button class="add-card" id="addCardBtn">
					<i class="fas fa-plus"></i> Add New Card
				</button>
			</div>

			<div class="list">
				<%
					List<Cardinfo> cards = (List<Cardinfo>) request.getAttribute("card");
					if (cards != null && !cards.isEmpty()) {
						for (Cardinfo card : cards) {
							String cardNumber = "•••• •••• •••• " + card.getCardNumber().substring(card.getCardNumber().length() - 4);
							String expDate = String.format("%02d/%02d", card.getExpMonth(), card.getExpYear());
				%>
				<div class="card">
					<div class="number">
						<%= cardNumber %>
					</div>

					<div class="details">
						<div>
							<div class="label">Card Holder</div>
							<div>
								<%= card.getCardName() %>
							</div>
						</div>
						<div>
							<div class="label">Expiry Date</div>
							<div>
								<%= expDate %>
							</div>
						</div>
					</div>

					<div class="actions">
						<a href="${pageContext.request.contextPath}/user/card?action=update&id=<%= card.getId() %>"
							class="edit">Edit</a>
						<button class="delete" onclick="confirmDelete(<%= card.getId()%>)">Delete</button>
					</div>
				</div>
				<%
						}
					} else {
				%>
				<div class="empty-status">
					<i class="far fa-credit-card"></i>
					<h3>No Saved Cards</h3>
					<p>You haven't added any credit or debit cards yet. Add your first card to get started.</p>
				</div>
				<% } %>
			</div>
		</div>
	</div>

	<!-- Add/Edit Card Form -->
	<div class="add-container" id="addPopup">
		<div class="add-content">
			<span class="close-btn" id="closePopupBtn">&times;</span>
			<h2>
				<%= request.getAttribute("updateCard") !=null ? "Edit Card" : "Add New Card" %>
			</h2>
			<form onsubmit="return validateForm()" action="${pageContext.request.contextPath}/user/card" method="POST">
				<%
					if (request.getAttribute("updateCard") !=null) {
						Cardinfo updateCard = (Cardinfo)request.getAttribute("updateCard");
				%>
				<input type="hidden" name="id" value="<%= updateCard.getId() %>">
				<% } %>
				<input type="hidden" name="action" value="<%= request.getAttribute("updateCard") !=null ? "update" : "create" %>">

				<div class="add-info number">
					<label for="number">Card Number</label>
					<input type="text" id="number" name="number" value="${number}" maxlength="12">
					<span class="error-message">${numberError}</span>
				</div>
				<div class="add-info name">
					<label for="name">Card Holder Name</label>
					<input type="text" id="name" name="name" value="${name}">
					<span class="error-message">${nameError}</span>
				</div>
				<div class="add-info expiryDate">
					<label for="expiryDate">Expiry Date (MM/YY)</label>
					<input type="text" id="expiryDate" name="expiryDate" placeholder="MM/YY" value="${expiryDate}"
						maxlength="5">
					<span class="error-message">${expiryDateError}</span>
				</div>
				<button type="submit" class="btn">
					<%= request.getAttribute("updateCard") !=null ? "Save Changes" : "Add Card" %>
				</button>
			</form>
		</div>
	</div>

	<form id="deleteForm" method="POST" action="${pageContext.request.contextPath}/user/card" style="display:none;">
		<input type="hidden" name="action" value="delete">
		<input type="hidden" id="deleteId" name="id">
	</form>

	<script>
		window.addEventListener("DOMContentLoaded", () => {
			const addPopup = document.getElementById('addPopup');
			<%
				String[] errorFields = { "number", "name", "expiryDate"};
				for (String field : errorFields) {
					String error = (String) request.getAttribute(field + "Error");
					if (error != null) {
			%>
				addPopup.style.display = 'flex';
				showError('<%= field %>', '<%= error %>');
			<%
					}
				}

				if (request.getAttribute("updateCard") != null) {
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
<script src="${pageContext.request.contextPath}/scripts/user/card.js" type="module"></script>

</html>