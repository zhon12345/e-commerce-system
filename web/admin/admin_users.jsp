<%-- /admin/admin_users.jsp --%>
<%@ page import="Model.Users" %>

<%
    request.setAttribute("activeAdminPage", "users");
    request.setAttribute("pageTitle", "User Management");
    request.setAttribute("mainContentPage", "/admin/content/users_content.jsp");
%>

<%-- Include the main layout template --%>
<jsp:include page="/admin/admin_layout.jsp" />
