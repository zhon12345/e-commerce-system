<%--
Document   : index
    Created on : 31 Mar 2025, 3:08:46 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Home Page</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/index.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/popup.css">
    </head>
    <header>
        <%@include file="h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- success message popup -->
        <% if (session.getAttribute("loginSuccess") != null && session.getAttribute("loginSuccess").equals("true")) { %>
        <div class="overlay show" id="overlay"></div>
        <div class="popup show" id="popup">
            <div class="success-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <div class="success-title">Login Successful!</div>
            <button class="close" onclick="closePopup()">OK</button>
            <% session.removeAttribute("loginSuccess"); %>
        </div>
            <% } %>

        <% if (session.getAttribute("logoutSuccess") != null && session.getAttribute("logoutSuccess").equals("true")) { %>
        <div class="overlay show" id="overlay"></div>
        <div class="popup show" id="popup">
            <div class="success-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <div class="success-title">Logout Successful!</div>
            <button class="close" onclick="closePopup()">OK</button>
            <% session.removeAttribute("logoutSuccess"); %>
        </div>
            <% } %>

        <img src="pic/pic_homepage/web_image.jpg" class="web_img">

        <!--Categories-->
        <div class="category-section">
            <h2 class="category-title">Category</h2>

            <div class="categories">
                <div class="category-item">
                    <img src="pic/pic_homepage/iem.jpg">
                    <span class="category-name">IEM</span>
                </div>

                <div class="category-item">
                    <img src="pic/pic_homepage/mouse.png">
                    <span class="category-name">Mouse</span>
                </div>

                <div class="category-item">
                    <img src="pic/pic_homepage/keyboard.png">
                    <span class="category-name">Keyboard</span>
                </div>
            </div>
        </div>

        <script>
            <!-- pop up -->
            function closePopup() {
                const popup = document.getElementById('popup');
                const overlay = document.getElementById('overlay');

                if (popup && overlay) {
                    popup.classList.remove('show');
                    overlay.classList.remove('show');
                }
            }
        </script>
    </body>
    <footer>
        <%@include file="h&f/footer.jsp" %>
    </footer>
</html>