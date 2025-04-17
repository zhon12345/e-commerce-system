<%--
    Document   : shop
    Created on : 1 Apr 2025, 3:19:32 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Shop Page</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/shop.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
    </head>
    <header>
        <%@include file="../h&f/navbar.jsp" %>
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
                    <button class="apply" style="margin-top: 10px;">Apply Rating</button>
                </div>
            </div>

            <!--Products-->
            <div class="products-section">
                <div class="products-grid">
                    <div class="card">
                        <div class="image">
                            <img src="${pageContext.request.contextPath}/pic/pic_products/iem/Aful Explorer 3.png">
                        </div>
                        <div class="name">Aful Explorer 1</div>
                        <div class="price">RM 199.00</div>
                        <div class="product-rating">
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                            <i class="fa-regular fa-star"></i>
                        </div>
                        <button class="add-to-cart">Add to Cart</button>
                    </div>

                    <div class="card">
                        <div class="image">
                            <img src="${pageContext.request.contextPath}/pic/pic_products/mouse/G pro hero 4.png">
                        </div>
                        <div class="name">Logitech G Pro Hero</div>
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

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const stars = document.querySelectorAll('.star');
                const selectedRatingInput = document.getElementById('selectedRating');
                let currentRating = 0;
                let selectedRating = 0;

                // Handle star click
                stars.forEach(star => {
                    star.addEventListener('click', function () {
                        const value = parseInt(this.getAttribute('data-value'));
                        selectedRating = value;
                        currentRating = value;
                        selectedRatingInput.value = value;
                        updateStars();
                    });
                });

                // Handle star hover
                stars.forEach(star => {
                    star.addEventListener('mouseover', function () {
                        const value = parseInt(this.getAttribute('data-value'));
                        currentRating = value;
                        updateStars();
                    });

                    star.addEventListener('mouseout', function () {
                        currentRating = selectedRating;
                        updateStars();
                    });
                });

                // Update star display
                function updateStars() {
                    stars.forEach(star => {
                        const value = parseInt(star.getAttribute('data-value'));
                        if (value <= currentRating) {
                            star.classList.add('hovered');
                            star.classList.remove('fa-regular');
                            star.classList.add('fa-solid');
                        } else {
                            star.classList.remove('hovered');
                            star.classList.remove('fa-solid');
                            star.classList.add('fa-regular');
                        }
                    });

                    // For selected stars (persistent)
                    stars.forEach(star => {
                        const value = parseInt(star.getAttribute('data-value'));
                        if (value <= selectedRating) {
                            star.classList.add('selected');
                        } else {
                            star.classList.remove('selected');
                        }
                    });
                }
            });
        </script>
    </body>
    <footer>
        <%@include file="../h&f/footer.jsp" %>
    </footer>
</html>