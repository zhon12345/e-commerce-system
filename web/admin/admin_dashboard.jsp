<%
    request.setAttribute("activeAdminPage", "dashboard");
    request.setAttribute("pageTitle", "Dashboard");
    request.setAttribute("mainContentPage", "/admin/content/dashboard_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />
