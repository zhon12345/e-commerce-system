<%--
    Document   : products
    Created on : 18 Apr 2025, 4:01:45 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Products</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/products.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
        <script>
            function switchTab(tabName) {
                // Hide all content sections
                document.querySelectorAll('.content-section').forEach(section => {
                    section.style.display = 'none';
                });

                // Remove active class from all tabs
                document.querySelectorAll('.tab').forEach(tab => {
                    tab.classList.remove('active');
                });

                // Show the selected content section
                document.getElementById(tabName + '-content').style.display = 'block';

                // Add active class to the clicked tab
                event.currentTarget.classList.add('active');
            }
        </script>
    </head>
    <header>
        <%@include file="components/navbar.jsp" %>
    </header>
    <body>
        <div class="title">
            <h2>SHOP</h2>
        </div>

        <div class="container">
            <div class="product-container">
                <!-- Left Box (Product Image) -->
                <div class="left-box">
                    <img src="${pageContext.request.contextPath}/assets/products/iem/Aful Explorer 3.png" alt="Product Image" class="product-image">
                </div>

                <!-- Right Box (Product Details) -->
                <div class="right-box">
                    <div class="product-header">AFUL Explorer 3</div>
                    <div class="product-subheader">High-resolution in-ear monitors for professional audio</div>
                    <div class="product-price">PM 199.99</div>

                    <div class="quantity">
                        <span class="quantity-label">Quantity:</span>
                        <div class="quantity-available">12 Available</div>

                        <div class="quantity-controls">
                            <button class="quantity-btn">-</button>
                            <input type="text" class="quantity-input" value="1">
                            <button class="quantity-btn">+</button>
                        </div>

                        <button class="add-to-cart">Add to cart</button>
                    </div>
                </div>
            </div>

            <!-- Bottom Box (Description/Review) -->
            <div class="bottom-box">
                <div class="tab-container">
                    <div class="tab active" onclick="switchTab('description')">Description</div>
                    <div class="tab" onclick="switchTab('reviews')">Reviews</div>
                </div>

                <div id="description-content" class="content-section" style="display: block;">
                    <div class="product-description">
                        <h3>Product Description</h3>
                        <p>The AFUL Explorer 3 in-ear monitors deliver professional-grade audio quality with a triple driver hybrid design. These IEMs combine dynamic drivers with balanced armatures to produce a rich, detailed soundstage with exceptional clarity across all frequencies.</p>
                    </div>
                </div>

                <div id="reviews-content" class="content-section" style="display: none;">
                    <div class="review-section">
                        <h3>Your Review</h3>
                        <textarea class="review-textarea" placeholder="Your Review...."></textarea>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="components/footer.jsp" %>
    </footer>
</html>
