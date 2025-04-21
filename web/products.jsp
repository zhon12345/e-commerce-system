<%--
    Document   : products
    Created on : 22 Apr 2025, 12:52:11 am
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        <%@include file="components/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>SHOP</h2>
        </div>

        <!--Filter-->
        <div class="container">
            <div class="box">
                <!-- option for category -->
                <div class="filter">
                    <h3>Product Category</h3>
                    <div class="option">
                        <input type="checkbox" id="iem">
                        <label for="iem">IEM</label>
                    </div>
                    <div class="option">
                        <input type="checkbox" id="mouse">
                        <label for="mouse">Mouse</label>
                    </div>
                    <div class="option">
                        <input type="checkbox" id="keyboard">
                        <label for="keyboard">Keyboard</label>
                    </div>
                </div>

                <!-- price range -->
                <div class="filter">
                    <h3>Price Range</h3>
                    <div class="range">
                        <input type="number" id="minPrice" placeholder="Min" min="0">
                        <span>to</span>
                        <input type="number" id="maxPrice" placeholder="Max" min="0">
                    </div>
                    <button class="apply">Apply</button>
                </div>

                <!-- star rating for filter -->
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
            </div>

            <!--Products-->
            <div class="products-section">
                <div class="products-grid">
                    <div class="card">
                        <a href="${pageContext.request.contextPath}/product.jsp" class="link">
                            <div class="image">
                                <img src="${pageContext.request.contextPath}/assets/products/iem/Aful Explorer 3.png">
                            </div>
                            <div class="name">IEM Aful Explorer 1</div>
                            <div class="price">RM 199.00</div>
                            <div class="product-rating">
                                <i class="fa-regular fa-star"></i>
                                <i class="fa-regular fa-star"></i>
                                <i class="fa-regular fa-star"></i>
                                <i class="fa-regular fa-star"></i>
                                <i class="fa-regular fa-star"></i>
                            </div>
                        </a>
                        <button class="add-to-cart">Add to Cart</button>
                    </div>

                    <div class="card">
                        <div class="image">
                            <img src="${pageContext.request.contextPath}/assets/products/mouse/G pro hero 4.png">
                        </div>
                        <div class="name">Mouse Logitech G Pro Hero</div>
                        <div class="price">RM 299.00</div>
                        <div class="product-rating">
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                        </div>
                        <button class="add-to-cart">Add to Cart</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="components/footer.jsp" %>
    </footer>
    <script src="${pageContext.request.contextPath}/scripts/products.js"></script>
</html>
