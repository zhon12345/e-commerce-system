<%@ page import="Model.Users" %>

<%
    String activePage = (String) request.getAttribute("activeAdminPage");
    if (activePage == null) {
        activePage = "dashboard";
    }

    Boolean isManager = (Boolean) session.getAttribute("isManager");
    if (isManager == null) {
        isManager = false;
    }
%>
<div class="sidebar mb-3">
    <h3 class="d-flex align-items-center gap-2"><i class="fas fa-tachometer-alt"></i> Menu</h3>
    <nav class="nav nav-pills flex-column" id="admin-sidebar-nav">
        <a class="nav-link <%= "dashboard".equals(activePage) ? "active" : "" %>"
           href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-chart-line"></i> Dashboard
        </a>
        <a class="nav-link <%= "users".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/users">
            <i class="fas fa-users-cog"></i> Users
        </a>
        <% if (isManager) { %>
            <a class="nav-link <%= "staff".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/staff">
                <i class="fas fa-user-shield"></i> Staff
            </a>
        <% } %>
        <a class="nav-link <%= "products".equals(activePage) ? "active" : "" %>" href="<%= request.getContextPath() %>/admin/products">
            <i class="fas fa-box-open"></i> Products
        </a>
        <a class="nav-link <%= "promotions".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/promotions">
            <i class="fas fa-tags"></i> Promotions
        </a>
        <a class="nav-link <%= "orders".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/orders">
            <i class="fas fa-receipt"></i> Orders
        </a>
        <a class="nav-link <%= "reviews".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/reviews">
            <i class="fa-solid fa-comment-dots"></i> Reviews
        </a>
    </nav>
</div>
