<%-- /WEB-INF/views/admin/content/orders_content.jsp --%>
<%-- This file contains only the content specific to the Order Management page --%>
<%-- /WEB-INF/views/admin/content/orders_content.jsp --%>
<%@ page import="java.util.List, Model.Orders, Model.Orderdetails, java.text.SimpleDateFormat" %>

<% if (session.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <%= session.getAttribute("error") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("error"); %>
<% } %>

<% if (session.getAttribute("success") != null) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <%= session.getAttribute("success") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% session.removeAttribute("success"); %>
<% } %>

<h2 class="mb-3 border-bottom pb-2 text-body"><i class="fas fa-receipt"></i> Order Management</h2>

<div class="table-responsive">
	<table class="table table-striped table-hover table-bordered align-middle">
		<thead class="table-light">
			<tr>
				<th>ID</th>
				<th>Customer</th>
				<th>Order Date</th>
				<th>Promo Code</th>
				<th>Discount</th>
				<th>Total Price</th>
				<th>Status</th>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<%
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				List<Orders> orders = (List<Orders>) request.getAttribute("orders");
				if (orders != null) {
					for (Orders order : orders) {
						String badge = "";
						if ("packaging".equals(order.getStatus())) {
								badge = "bg-warning text-dark";
						} else if ("shipping".equals(order.getStatus())) {
								badge = "bg-info";
						} else if ("delivery".equals(order.getStatus())) {
								badge = "bg-success";
						}
			%>
			<tr>
				<td><%= order.getId() %></td>
				<td><%= order.getUserId().getUsername() %></td>
				<td><%= dateFormat.format(order.getOrderDate()) %></td>
				<td><%= order.getPromoId() != null ? order.getPromoId().getPromoCode() : "N/A" %></td>
				<td><%= order.getPromoId() != null ? "RM " + order.getDiscount() : "N/A" %></td>
				<td>RM <%= order.getTotalPrice() %></td>
				<td><span class="badge <%= badge %>"><%= order.getStatus() %></span></td>
				<td>
					<button class="btn btn-sm btn-success action-btn" data-bs-toggle="modal" data-bs-target="#viewDetailsModal"
						data-order-id="<%= order.getId() %>"
						data-customer="<%= order.getUserId().getUsername() %>"
        		data-order-date="<%= dateFormat.format(order.getOrderDate()) %>"
        		data-promo-code="<%= order.getPromoId() != null ? order.getPromoId().getPromoCode() : "N/A" %>"
        		data-total-price="RM <%= order.getTotalPrice() %>"
						data-order-details='<%
							List<Orderdetails> details = order.getOrderdetailsList();
							StringBuilder json = new StringBuilder("[");
							for (int i = 0; i < details.size(); i++) {
								Orderdetails od = details.get(i);
								String productName = od.getProductId().getName().replace("\"", "\\\"");
								json.append("{")
									.append("\"product\":\"").append(productName).append("\",")
									.append("\"quantity\":").append(od.getQuantity()).append(",")
									.append("\"price\":").append(od.getPrice())
									.append("}");
								if (i < details.size() - 1) json.append(",");
							}
							json.append("]");
							out.print(json.toString());
						%>'
					>
						<i class="fas fa-eye"></i>
					</button>

					<button class="btn btn-sm btn-info action-btn" data-bs-toggle="modal" data-bs-target="#updateStatusModal" data-order-id="<%= order.getId() %>" data-current-status="<%= order.getStatus() %>">
						<i class="fas fa-edit"></i>
					</button>
				</td>
			</tr>
			<%
					}
				} else {
			%>
			<tr>
				<td colspan="8" class="text-center">No orders found.</td>
			</tr>
			<% } %>
		</tbody>
	</table>
</div>

<div class="modal fade" id="viewDetailsModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Order Details - #<span id="viewOrderId"></span></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="row mb-3">
          <div class="col-md-6">
            <h6>Customer:</h6>
            <p id="viewCustomer"></p>
          </div>
          <div class="col-md-6">
            <h6>Order Date:</h6>
            <p id="viewOrderDate"></p>
          </div>
        </div>

        <h6>Items:</h6>
        <div id="viewItems" class="mb-3"></div>

        <div class="row">
          <div class="col-md-6">
            <h6>Promo Code:</h6>
            <p id="viewPromoCode"></p>
          </div>
          <div class="col-md-6">
            <h6>Total Price:</h6>
            <p id="viewTotalPrice"></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="updateStatusModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Update Order Status - #<span id="updateOrderId"></span></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form action="${pageContext.request.contextPath}/admin/orders" method="POST">
        <input type="hidden" name="orderId" id="updateOrderIdInput">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Status</label>
            <select name="status" class="form-select" required>
              <option value="packaging">Packaging</option>
              <option value="shipping">Shipping</option>
              <option value="delivery">Delivery</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          <button type="submit" class="btn btn-primary">Update Status</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
document.getElementById('viewDetailsModal').addEventListener('show.bs.modal', function(event) {
	const button = event.relatedTarget;
	const orderId = button.getAttribute('data-order-id');
	const customer = button.getAttribute('data-customer');
	const orderDate = button.getAttribute('data-order-date');
	const promoCode = button.getAttribute('data-promo-code');
	const totalPrice = button.getAttribute('data-total-price');
	const orderDetails = JSON.parse(button.getAttribute('data-order-details'));

	document.getElementById('viewOrderId').textContent = orderId;
	document.getElementById('viewCustomer').textContent = customer;
	document.getElementById('viewOrderDate').textContent = orderDate;
	document.getElementById('viewPromoCode').textContent = promoCode;
	document.getElementById('viewTotalPrice').textContent = totalPrice;

	const itemsContainer = document.getElementById('viewItems');
	itemsContainer.innerHTML = '';

	console.log(orderDetails)

	orderDetails.forEach(item => {
    const itemHtml = `
			<div class="card mb-2">
				<div class="card-body">
					<h6 class="card-title">\${item.product}</h6>
					<p class="card-text">Quantity: \${item.quantity}</p>
					<p class="card-text">Price: RM \${parseFloat(item.price).toFixed(2)}</p>
				</div>
			</div>
    `;
    itemsContainer.insertAdjacentHTML('beforeend', itemHtml);
	});
});

document.getElementById('updateStatusModal').addEventListener('show.bs.modal', function(event) {
	const button = event.relatedTarget;
	const orderId = button.dataset.orderId;
	const currentStatus = button.dataset.currentStatus;

	document.getElementById('updateOrderId').textContent = orderId;
	document.getElementById('updateOrderIdInput').value = orderId;
	document.querySelector('#updateStatusModal select[name="status"]').value = currentStatus;
});
</script>