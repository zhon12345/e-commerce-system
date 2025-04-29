<div class="sidebar">
	<h3>My Account</h3>
	<ul>
	<% String activePage = request.getParameter("activePage") != null ? request.getParameter("activePage") : "profile"; %>
		<li><a href="profile.jsp" class="<%= activePage.equals("profile") ? "active" : "" %>">Profile</a></li>
		<li><a href="address" class="<%= activePage.equals("address") ? "active" : "" %>">Address</a></li>
		<li><a href="card" class="<%= activePage.equals("card") ? "active" : "" %>">Bank & Card</a></li>
		<li><a href="history" class="<%= activePage.equals("history") ? "active" : "" %>">History</a></li>
		<li><a href="reviews" class="<%= activePage.equals("reviews") ? "active" : "" %>">Reviews</a></li>
	</ul>
</div>