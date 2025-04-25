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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/index.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/popup.css">
    </head>
    <header>
        <%@include file="components/navbar.jsp" %>
    </header>
    <body>
        <img src="assets/home/hero.jpg" class="web_img">

        <!--Categories-->
        <div class="category-section">
            <h2 class="category-title">Category</h2>

            <div class="categories">
                <div class="category-item">
                    <img src="assets/home/iem.jpg">
                    <span class="category-name">IEM</span>
                </div>

                <div class="category-item">
                    <img src="assets/home/mouse.png">
                    <span class="category-name">Mouse</span>
                </div>

                <div class="category-item">
                    <img src="assets/home/keyboard.png">
                    <span class="category-name">Keyboard</span>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="components/footer.jsp" %>
    </footer>
</html>