<%--
    Document   : checkout
    Created on : 21 Apr 2025, 12:51:39 pm
    Author     : yjee0
--%>

<%@ page import="java.util.List, Model.Addresses, Model.Cardinfo, Model.Promotions"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Checkout</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/empty_status.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/checkout.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<header>
	<%@include file="components/navbar.jsp" %>
</header>

<body>
	<div class="title">
		<h2>Checkout</h2>
	</div>

	<div class="container">
		<!-- Combined Box for All Sections -->
		<div class="checkout-box">
			<!-- Address Section -->
			<div class="address">
				<h3 class="section-title">Select Delivery Address</h3>
				<%
					List<Addresses> addressList = (List<Addresses>) request.getAttribute("addressList");
					if(addressList != null && !addressList.isEmpty()) {
				%>
				<div class="addressSelector">
					<select class="address-dropdown" id="addressSelector">
						<option value="" disabled selected>Select an address</option>
						<%
							for(Addresses address : addressList) {
						%>
						<option value="<%= address.getId() %>" data-receiver-name="<%= address.getReceiverName() %>" data-contact-number="<%= address.getContactNumber() %>" data-address1="<%= address.getAddress1() %>" data-address2="<%= address.getAddress2() %>" data-postal-code="<%= address.getPostalCode() %>" data-city="<%= address.getCity() %>" data-state="<%= address.getState() %>">
							<%= address.getReceiverName() %> - <%= address.getAddress1() %><%= !address.getAddress2().isEmpty() ? ", " + address.getAddress2() : "" %>, <%= address.getPostalCode() %> <%= address.getCity() %>, <%= address.getState() %>
						</option>
						<% } %>
					</select>
					<span class="error-message"></span>
				</div>

				<div class="address-details">
					<div class="small">
						<input type="text" class="address-input" name="receiverName" placeholder="Receiver's Name" readonly />
						<input type="text" class="address-input" name="contactNumber" placeholder="Contact Number" readonly />
					</div>

					<input type="text" class="address-input" name="address1" placeholder="Address Line 1" readonly />
					<input type="text" class="address-input" name="address2" placeholder="Address Line 2" readonly />

					<div class="small">
						<input type="text" class="address-input" name="postalCode" placeholder="Postcode" readonly />
						<input type="text" class="address-input" name="city" placeholder="City" readonly />
						<input type="text" class="address-input" name="state" placeholder="State" readonly />
					</div>
				</div>
				<% } else { %>
				<div class="empty-status address">
					<i class="fas fa-map-marker-alt"></i>
					<h3>No Saved Addresses</h3>
					<p>You haven't added any addresses yet. Please add an address.</p>
					<a href="${pageContext.request.contextPath}/user/address">
						<button class="add-address">Add Address</button>
					</a>
				</div>
				<% } %>
			</div>

			<!-- Payment Information Section -->
			<div class="payment-info">
				<h3 class="section-title">Payment Information</h3>
				<div class="payment-methods">
					<label>
						<input type="radio" name="payment-method" id="cash" value="cash" ${selectedPaymentMethod eq 'cash' ? 'checked' : ''}> Cash
					</label>
					<label>
						<input type="radio" name="payment-method" id="credit-card" value="card" ${selectedPaymentMethod eq 'card' ? 'checked' : ''}> Credit Card
					</label>
					<label>
						<input type="radio" name="payment-method" id="e-wallet" value="e-wallet" ${selectedPaymentMethod eq 'e-wallet' ? 'checked' : ''}> E-Wallet
					</label>
				</div>

				<div class="credit-card" id="creditCardSection">
					<%
						List<Cardinfo> cardList = (List<Cardinfo>) request.getAttribute("cardList");
						if(cardList != null && !cardList.isEmpty()) {
					%>
					<div class="cardSelector">
						<select class="card-select" id="cardSelector">
							<option value="" disabled selected>Select a card</option>
							<%
								for(Cardinfo card : cardList) {
									String maskedNumber="**** **** **** " + card.getCardNumber().substring(card.getCardNumber().length() - 4);
							%>
							<option value="<%= card.getId() %>" data-card-number="<%= card.getCardNumber() %>" data-card-name="<%= card.getCardName() %>" data-exp-month="<%= card.getExpMonth() %>" data-exp-year="<%= card.getExpYear() %>"> <%= maskedNumber %> (<%= card.getCardName() %>)</option>
							<% } %>
						</select>
						<span class="error-message"></span>
					</div>

					<div class="card-details">
						<input type="text" class="card-input" name="cardHolder" placeholder="Card Holder" readonly />
						<input type="text" class="card-input" name="cardNumber" placeholder="Card Number" readonly />

						<div class="small">
							<div class="expDate">
								<input type="text" class="card-input" name="expDate" placeholder="Expiration Date" maxlength=5 readonly />
							</div>
							<div class="cvv">
								<input type="password" class="card-input" name="cvv" id="cvv" placeholder="CVV" value="${enteredCvv}" maxlength=3/>
								<span class="error-message"></span>
							</div>
						</div>
					</div>
					<% } else { %>
					<div class="empty-status card">
						<i class="far fa-credit-card"></i>
						<h3>No Saved Cards</h3>
						<p>You haven't added any credit or debit cards yet. Please add a card.</p>
						<a href="${pageContext.request.contextPath}/user/card">
							<button class="add-card">Add Card</button>
						</a>
					</div>
					<% } %>
				</div>
			</div>

			<!-- Promotion Code Section -->
			<div class="promotion">
				<h3 class="section-title">Promo Code</h3>
				<%
					Promotions appliedPromo = (Promotions) session.getAttribute("appliedPromo");
					if (appliedPromo != null) {
				%>
				<form action="${pageContext.request.contextPath}/checkout" method="get" class="applied-promo">
					<input type="text" class="promo-code-input" name="removePromo" value="<%= appliedPromo.getPromoCode() %>" readonly>
					<button type="submit" class="remove-promo">Remove</button>
				</form>
				<% } else { %>
				<form action="${pageContext.request.contextPath}/checkout" method="get" class="promoCode">
					<div>
						<input type="text" class="promo-code-input" name="promoCode" id="promoCode" placeholder="Enter promo code" value="${param.promoCode}">
						<button type="submit" class="apply-promo">Apply</button>
					</div>
					<span class="error-message">${promoError}</span>
				</form>
				<% } %>
			</div>

			<!-- Order Summary Section -->
			<div class="summary-detail">
				<span>Subtotal (${totalItems != null ? totalItems : 0} items)</span>
				<span>RM ${subtotal}</span>
			</div>
			<% if (appliedPromo != null) { %>
			<div class="summary-detail">
				<span>Discount (<%= appliedPromo.getPromoCode() %>)</span>
				<span>- RM ${discount}</span>
			</div>
			<% } %>
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
			<form onsubmit="return validateForm()" action="${pageContext.request.contextPath}/checkout" method="post" id="orderForm">
				<input type="hidden" name="addressId" id="selectedAddressId">
				<input type="hidden" name="paymentMethod" id="selectedPaymentMethod">
				<input type="hidden" name="cardId" id="selectedCardId">
				<input type="hidden" name="cvv" id="enteredCvv">
				<button type="submit" class="place-order">Place Order</button>
			</form>
		</div>
	</div>
	<script>
		window.addEventListener("DOMContentLoaded", () => {
		<%
			String[] errorFields = { "addressSelector", "cardSelector", "cvv", "promoCode"};
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

		window.initialState = {
			selectedPaymentMethod: "${not empty selectedPaymentMethod ? selectedPaymentMethod : 'cash'}",
			selectedAddressId: "${selectedAddressId}",
			selectedCardId: "${selectedCardId}",
			enteredCvv: "${enteredCvv}"
		};
	</script>
	<script src="${pageContext.request.contextPath}/scripts/checkout.js" type="module"></script>
</body>
<footer>
	<%@include file="components/footer.jsp" %>
</footer>

</html>