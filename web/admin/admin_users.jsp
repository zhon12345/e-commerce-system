<%-- /admin/admin_users.jsp --%>
<%@ page import="Model.Users" %>

<%-- Set attributes for the layout template --%>
<%
    // Get the current logged in user to check if they're a manager
    boolean isManager = false;
    Users currentUser = (Users) session.getAttribute("user");
    if (currentUser != null) {
        isManager = "manager".equals(currentUser.getRole());
    }

    request.setAttribute("activeAdminPage", "users");
    request.setAttribute("pageTitle", "User Management");
    request.setAttribute("mainContentPage", "/admin/content/users_content.jsp");

    // Pass the isManager flag to the content template
    request.setAttribute("isManager", isManager);
%>

<%-- Include the main layout template --%>
<jsp:include page="/admin/admin_layout.jsp" />
