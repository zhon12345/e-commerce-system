<%
    request.setAttribute("activeAdminPage", "products");
    request.setAttribute("pageTitle", "Product Management");
    request.setAttribute("mainContentPage", "/admin/content/products_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />
