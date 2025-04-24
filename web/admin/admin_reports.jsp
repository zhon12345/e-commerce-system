<%-- /admin/admin_reports.jsp --%>
<%-- Set attributes for the layout template --%>
<%
    request.setAttribute("activeAdminPage", "reports");
    request.setAttribute("pageTitle", "View Reports");
    // Specify the path to the content fragment for this page
    request.setAttribute("mainContentPage", "/admin/content/reports_content.jsp");

    // --- TODO: Add backend logic here to fetch reportList and set it as a request attribute ---
    // Example: List<Reports> reportList = reportDAO.getAllReports();
    // request.setAttribute("reportList", reportList);
%>

<%-- Include the main layout template --%>
<jsp:include page="/admin/admin_layout.jsp" />
