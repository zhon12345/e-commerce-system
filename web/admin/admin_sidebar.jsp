<%-- /admin/admin_sidebar.jsp --%>
<%
    // Get the active page name set by the including JSP
    String activePage = (String) request.getAttribute("activeAdminPage");
    if (activePage == null) {
        activePage = "dashboard"; // Default if not set
    }

    // Check manager status (needed to conditionally show staff link)
    // Staff loggedInStaff = (Staff) session.getAttribute("loggedInStaff");
    boolean isManager = false; // Placeholder
    // if (loggedInStaff != null) {
    //     // isManager = loggedInStaff.getIsManager();
    // }
%>
<div class="sidebar mb-3">
    <h3 class="d-flex align-items-center gap-2"><i class="fas fa-tachometer-alt"></i> Menu</h3>
    <nav class="nav nav-pills flex-column" id="admin-sidebar-nav">
        <%-- Use standard links to other JSP pages --%>
        <a class="nav-link <%= "dashboard".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/admin_dashboard.jsp">
            <i class="fas fa-chart-line"></i> Dashboard
        </a>
        <a class="nav-link <%= "users".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/admin_users.jsp">
            <i class="fas fa-users-cog"></i> Users
        </a>
        <a class="nav-link <%= "products".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/admin_products.jsp">
            <i class="fas fa-box-open"></i> Products
        </a>
        <a class="nav-link <%= "promotions".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/admin_promotions.jsp">
            <i class="fas fa-tags"></i> Promotions
        </a>
        <a class="nav-link <%= "orders".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/admin_orders.jsp">
            <i class="fas fa-receipt"></i> Orders
        </a>
        <a class="nav-link <%= "reports".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/admin_reports.jsp">
            <i class="fas fa-file-alt"></i> Reports
        </a>
        <%-- Conditionally show Staff Management for Managers --%>
        <%-- <% if (isManager) { %> --%>
            <a class="nav-link <%= "staff".equals(activePage) ? "active" : "" %>" href="${pageContext.request.contextPath}/admin/admin_staff.jsp">
                <i class="fas fa-user-shield"></i> Staff
            </a>
        <%-- <% } %> --%>
    </nav>
</div>
