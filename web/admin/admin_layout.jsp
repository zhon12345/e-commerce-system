<%@ page import="java.util.List, Utils.Authentication, jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, Model.Products, Model.Categories, Model.Users"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Users currentUser = (Users) session.getAttribute("user");
    boolean isManager = false;
    boolean isStaff = false;

    if (currentUser != null && currentUser.getRole() != null) {
        String role = currentUser.getRole();
        isManager = "manager".equalsIgnoreCase(role);
        isStaff = "staff".equalsIgnoreCase(role);

        session.setAttribute("isManager", isManager);
    }

    if (!Authentication.isLoggedInAndAuthorized(request, response, currentUser, (String) null)) return;
%>

<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Panel - ${param.pageTitle != null ? param.pageTitle : 'Dashboard'}</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin_dashboard_styles.css">
</head>

<body>
  <nav class="navbar navbar-expand-lg bg-body-tertiary mb-4 shadow-sm">
    <div class="container-fluid">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
        <i class="fas fa-shield-alt me-2"></i>Admin Panel
      </a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar"
        aria-controls="adminNavbar" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="adminNavbar">
        <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
          <li class="nav-item">
            <span class="navbar-text me-3">
              Welcome, <%= session.getAttribute("user") !=null ? ((Users)session.getAttribute("user")).getUsername()
                : "Admin User" %>!
            </span>
          </li>
          <li class="nav-item me-2">
            <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/admin/profile">
              <i class="fas fa-user-circle"></i> My Profile
            </a>
          </li>
          <li class="nav-item">
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/logout">
              <i class="fas fa-sign-out-alt"></i> Logout
            </a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <div class="container-fluid">
    <div class="row">
      <div class="col-lg-2 col-md-3">
        <jsp:include page="/admin/admin_sidebar.jsp" />
      </div>
      <div class="col-lg-10 col-md-9">
        <div class="bg-white p-4 rounded shadow-sm mb-4">
          <jsp:include page="${mainContentPage}" />
        </div>
      </div>
    </div>
  </div>
  <div class="toast-container position-fixed top-0 end-0 p-3">
  </div>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
    integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
    crossorigin="anonymous"></script>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin/dashboard_data.js"></script>
</body>

</html>