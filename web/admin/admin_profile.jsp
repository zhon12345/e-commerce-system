<%@ page import="Model.Users" %>

<%
    request.setAttribute("activeAdminPage", "profile");
    request.setAttribute("pageTitle", "My Profile");
    request.setAttribute("mainContentPage", "/admin/content/profile_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />