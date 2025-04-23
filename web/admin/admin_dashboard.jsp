<%-- /admin/admin_dashboard.jsp --%>
<%-- Set attributes for the layout template --%>
<%
    request.setAttribute("activeAdminPage", "dashboard");
    request.setAttribute("pageTitle", "Dashboard");
    // Specify the path to the content fragment for this page
    request.setAttribute("mainContentPage", "/WEB-INF/views/admin/content/dashboard_content.jsp");
%>

<%-- Include the main layout template --%>
<jsp:include page="/WEB-INF/views/admin/admin_layout.jsp" />
