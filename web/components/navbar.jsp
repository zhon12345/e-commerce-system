<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Navbar</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <style>
            /* Minimal CSS for search toggle */
            .search-container {
                position: relative;
                margin-right: 1rem;
            }
            .search-input {
                width: 0;
                opacity: 0;
                transition: all 0.3s ease;
                position: absolute;
                right: 40px;
            }
            .search-input.expanded {
                width: 500px;
                opacity: 1;
                padding: 8px 15px;
            }
        </style>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-light bg-white">
            <div class="container">
                <!-- Brand/Logo -->
                <a class="navbar-brand me-5" href="${pageContext.request.contextPath}/index.jsp">
                    <img src="${pageContext.request.contextPath}/assets/logo/text.png" height="40">
                </a>

                <!-- Mobile Toggle -->
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <!-- Navbar Content -->
                <div class="collapse navbar-collapse" id="navbarContent">
                    <!-- Navigation Links -->
                    <ul class="navbar-nav me-auto">
                        <li class="nav-item mx-2">
                            <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">Home</a>
                        </li>
                        <li class="nav-item mx-2">
                            <a class="nav-link" href="${pageContext.request.contextPath}/shop.jsp">Shop</a>
                        </li>
                        <li class="nav-item mx-2">
                            <a class="nav-link" href="${pageContext.request.contextPath}/about.jsp">About Us</a>
                        </li>
                        <li class="nav-item mx-2">
                            <a class="nav-link" href="${pageContext.request.contextPath}/contact.jsp">Contact</a>
                        </li>
                    </ul>

                    <!-- Right-aligned Items -->
                    <div class="d-flex align-items-center">
                        <!-- Search Icon -->
                        <div class="search-container">
                            <input type="text" class="form-control search-input" placeholder="Search...">
                            <button class="btn btn-link text-dark" id="searchToggle">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>

                        <!-- Login Button -->
                        <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary px-4">
                            Login / Sign Up
                        </a>
                    </div>
                </div>
            </div>
        </nav>

        <!-- Bootstrap JS Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const searchToggle = document.getElementById('searchToggle');
                const searchInput = document.querySelector('.search-input');

                // Toggle search input
                searchToggle.addEventListener('click', function () {
                    searchInput.classList.toggle('expanded');
                    if (searchInput.classList.contains('expanded')) {
                        searchInput.focus();
                    }
                });

                // Close search when clicking outside
                document.addEventListener('click', function (e) {
                    if (!e.target.closest('.search-container') && e.target !== searchToggle) {
                        searchInput.classList.remove('expanded');
                    }
                });

                // Handle search on Enter key
                searchInput.addEventListener('keypress', function (e) {
                    if (e.key === 'Enter') {
                        // Perform search action here
                        alert('Searching for: ' + searchInput.value);
                    }
                });
            });
        </script>
    </body>
</html>