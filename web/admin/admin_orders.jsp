<%
    request.setAttribute("activeAdminPage", "orders");
    request.setAttribute("pageTitle", "Order Management");
    request.setAttribute("mainContentPage", "/admin/content/orders_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />
