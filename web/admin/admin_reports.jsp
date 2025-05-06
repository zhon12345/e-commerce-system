<%
    request.setAttribute("activeAdminPage", "reports");
    request.setAttribute("pageTitle", "View Reports");
    request.setAttribute("mainContentPage", "/admin/content/reports_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />
