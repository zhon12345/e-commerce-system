<%@ page import="Model.Users" %>

<%
    request.setAttribute("activeAdminPage", "users");
    request.setAttribute("pageTitle", "User Management");
    request.setAttribute("mainContentPage", "/admin/content/users_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />
