<%@ page import="Model.Users" %>

<%
    request.setAttribute("activeAdminPage", "staff");
    request.setAttribute("pageTitle", "Staff Management");
    request.setAttribute("mainContentPage", "/admin/content/staff_content.jsp");
%>

<jsp:include page="/admin/admin_layout.jsp" />