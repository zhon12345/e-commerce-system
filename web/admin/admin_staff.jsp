<%-- /admin/admin_staff.jsp --%>
<%-- Set attributes for the layout template --%>
<%
    request.setAttribute("activeAdminPage", "staff");
    request.setAttribute("pageTitle", "Staff Management");
    // Specify the path to the content fragment for this page
    request.setAttribute("mainContentPage", "${pageContext.request.contextPath}/admin/content/staff_content.jsp");

    // --- TODO: Add backend logic here to fetch staffList and set it as a request attribute ---
    // Example: List<Staff> staffList = staffDAO.getAllStaff();
    // request.setAttribute("staffList", staffList);
%>

<%-- Include the main layout template --%>
<jsp:include page="/admin/admin_layout.jsp" />
