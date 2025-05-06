<%
    request.setAttribute("activeAdminPage", "reviews");
    request.setAttribute("pageTitle", "Reviews Management");
    request.setAttribute("mainContentPage", "/admin/content/reviews_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />
