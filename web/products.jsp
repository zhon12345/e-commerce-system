<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List, Model.Products"%>
<%@page import="java.util.List, Model.Categories"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Shop Page</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/products.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
    </head>
    <header>
        <%@ include file="components/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>SHOP</h2>
        </div>

        <!-- Filter -->
        <div class="container">
            <form class="box" method="POST">
                <!-- Option for category -->
                <div class="filter">
                    <h3>Product Category</h3>
                    <%
                        List<Categories> categories = (List<Categories>) request.getAttribute("categories");
                        if (categories != null) {
                        for (Categories category : categories) {
                    %>
                    <div class="option">
                        <input type="checkbox" name="category" value="<%= category.getId() %>"
                               <label for="cat<%= category.getId() %>"><%= category.getName() %></label>
                    </div>
                    <%
   }
}
                    %>

                </div>

                <!-- Price range -->
                <div class="filter">
                    <h3>Price Range</h3>
                    <div class="range">
                        <input type="number" id="minPrice" placeholder="Min" min="0">
                        <span>to</span>
                        <input type="number" id="maxPrice" placeholder="Max" min="0">
                    </div>
                    <button class="apply">Apply</button>
                </div>

                <!-- Star rating for filter -->
                <div class="filter">
                    <h3>Rating</h3>
                    <div class="star-rating" id="starContainer">
                        <i class="fa-regular fa-star star" data-value="1"></i>
                        <i class="fa-regular fa-star star" data-value="2"></i>
                        <i class="fa-regular fa-star star" data-value="3"></i>
                        <i class="fa-regular fa-star star" data-value="4"></i>
                        <i class="fa-regular fa-star star" data-value="5"></i>
                    </div>
                    <div class="p-rating">Click stars to filter</div>
                    <input type="hidden" id="selectedRating" value="0">
                    <button class="apply">Apply Rating</button>
                </div>
            </form>

            <!-- Products -->
            <div class="products-section">
                <div class="products-grid">
                    <%
                        List<Products> products = (List<Products>) request.getAttribute("products");
                        if (products != null) {
                            for (Products product : products) {
                    %>
                    <div class="card">
                        <a href="<%= request.getContextPath() + "/product.jsp?id=" + product.getId() %>" class="link">
                            <div class="image">
                                <img src="${pageContext.request.contextPath}/assets/products/iem/Aful Explorer 3.png">
                            </div>
                            <div class="name"><%= product.getDescription() %></div>
                            <div class="price">RM <%= product.getPrice() %></div>
                            <div class="product-rating">
                                <% for (int i = 0; i < 5; i++) { %>
                                <i class="fa-regular fa-star"></i>
                                <% } %>
                            </div>
                        </a>
                        <button class="add-to-cart">Add to Cart</button>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@ include file="components/footer.jsp" %>
    </footer>
    <script src="${pageContext.request.contextPath}/scripts/products.js"></script>
</html>
