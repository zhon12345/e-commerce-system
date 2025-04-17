<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Navbar</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/navbar.css">
    </head>
    <body>
        <div class="navbar">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/pic/logo/logo.jpg" class="logo">
            </a>

            <div class="in-navbar">
                <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
                <a href="${pageContext.request.contextPath}/page/shop.jsp">Shop</a>
                <a href="${pageContext.request.contextPath}/page/aboutus.jsp">About Us</a>
                <a href="${pageContext.request.contextPath}/page/contact.jsp">Contact</a>

                <div class="search-container">
                    <input type="text" class="search-input" placeholder="Search products...">
                    <i class="fa fa-search search-icon" id="searchIcon" onclick="toggleSearch()"></i>
                    <i class="fa fa-times close-icon" id="closeIcon" onclick="closeSearch()"></i>
                </div>

                <% if (session.getAttribute("user") != null) { %>
                <div class="icon-container">
                    <i class="fa-solid fa-cart-shopping nav-icon" onclick="location.href='cart.jsp'"></i>
                    <div class="user-dropdown">
                        <i class="fas fa-user-circle nav-icon"></i>
                        <div class="dropdown-content">
                            <a href="${pageContext.request.contextPath}/page/profile/profile.jsp">My Profile</a>
                            <a href="${pageContext.request.contextPath}/page/profile/history.jsp">History</a>
                            <a href="${pageContext.request.contextPath}/index.jsp?logout=true">Logout</a>
                        </div>
                    </div>
                </div>
                <% } else { %>
                <a href="${pageContext.request.contextPath}/page/login_signup/login.jsp" class="login_signup-btn">Login / Sign Up</a>
                <% } %>
            </div>
        </div>

        <%
            if (request.getParameter("logout") != null) {
                session.setAttribute("logoutSuccess", "true");
                session.invalidate();
                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("logoutSuccess", "true");
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }
        %>

        <script>
            function toggleSearch() {
                const searchInput = document.querySelector('.search-input');
                const closeIcon = document.getElementById('closeIcon');

                searchInput.classList.toggle('active');
                closeIcon.classList.toggle('active');

                if (searchInput.classList.contains('active')) {
                    searchInput.focus();
                }
            }

            function closeSearch() {
                const searchInput = document.querySelector('.search-input');
                const closeIcon = document.getElementById('closeIcon');

                searchInput.classList.remove('active');
                closeIcon.classList.remove('active');
                searchInput.value = '';
            }

            document.addEventListener('click', function (e) {
                const searchContainer = document.querySelector('.search-container');
                const searchInput = document.querySelector('.search-input');
                const searchIcon = document.getElementById('searchIcon');
                const closeIcon = document.getElementById('closeIcon');

                if (!searchContainer.contains(e.target) && e.target !== searchIcon && e.target !== closeIcon) {
                    searchInput.classList.remove('active');
                    closeIcon.classList.remove('active');
                }
            });
        </script>
    </body>
</html>