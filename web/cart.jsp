<%--
    Document   : cart
    Created on : 18 Apr 2025, 11:13:09 am
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Shopping Cart</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/cart.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <header>
        <%@include file="components/navbar.jsp" %>
    </header>
    <body>
        <div class="title">
            <h2>Shopping Cart</h2>
        </div>

        <div class="container">
            <!-- Cart Items Section -->
            <div class="items">
                <div class="items-container">
                    <div class="header">
                        <div class="cart-title">Your Cart</div>
                        <div class="item-count">1 items</div>
                    </div>

                    <div class="item">
                        <img src="${pageContext.request.contextPath}/assets/products/iem/Aful Explorer 3.png" alt="Product" class="item-image">
                        <div class="item-details">
                            <div class="item-title">Premium Wireless Headphones</div>
                            <div class="item-price">RM 129.99</div>
                            <div class="quantity">
                                <button class="quantity-btn">-</button>
                                <input type="number" value="1" min="1" class="quantity-input">
                                <button class="quantity-btn">+</button>
                            </div>
                        </div>
                        <div>
                            <i class="fas fa-trash remove-btn"></i>
                        </div>
                    </div>

                    <div class="item">
                        <img src="${pageContext.request.contextPath}/assets/products/iem/Aful Explorer 3.png" alt="Product" class="item-image">
                        <div class="item-details">
                            <div class="item-title">Premium Wireless Headphones</div>
                            <div class="item-price">RM 129.99</div>
                            <div class="quantity">
                                <button class="quantity-btn">-</button>
                                <input type="number" value="1" min="1" class="quantity-input">
                                <button class="quantity-btn">+</button>
                            </div>
                        </div>
                        <div>
                            <i class="fas fa-trash remove-btn"></i>
                        </div>
                    </div>
                </div>

                <!-- Empty Cart State -->
                <!--
                <div class="items-container">
                    <div class="empty">
                        <i class="fas fa-shopping-cart"></i>
                        <h3>Your cart is empty</h3>
                        <p>Looks like you haven't added anything to your cart yet</p>
                        <a href="products.jsp" class="empty-btn">Continue Shopping</a>
                    </div>
                </div>
                -->
            </div>

            <!-- Order Summary Section -->
            <div class="summary">
                <h3 class="summary-title">Order Summary</h3>
                <div class="summary-detail">
                    <span>Subtotal (1 items)</span>
                    <span>$329.98</span>
                </div>
                <div class="summary-detail">
                    <span>Shipping</span>
                    <span>Free</span>
                </div>
                <div class="summary-detail">
                    <span>Tax</span>
                    <span>$19.80</span>
                </div>
                <div class="summary-total">
                    <span>Total</span>
                    <span>$349.78</span>
                </div>
                <button class="checkout-btn">Proceed to Checkout</button>
                <a href="products.jsp" class="back">Continue Shopping</a>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="components/footer.jsp" %>
    </footer>
</html>