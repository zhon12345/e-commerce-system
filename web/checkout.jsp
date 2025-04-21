<%--
    Document   : checkout
    Created on : 21 Apr 2025, 12:51:39 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Checkout</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/checkout.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <header>
        <%@include file="components/navbar.jsp" %>
    </header>
    <body>
        <div class="title">
            <h2>Checkout</h2>
        </div>

        <div class="container">
            <!-- Combined Box for All Sections -->
            <div class="checkout-box">
                <!-- Address Section -->
                <div class="address">
                    <h3 class="section-title">Select Delivery Address</h3>
                    <select class="address-dropdown">
                        <option value="1">123 Main St, Cityville, ABC 123</option>
                        <option value="2">456 Elm St, Townsville, XYZ 456</option>
                        <option value="3">789 Oak St, Villagetown, DEF 789</option>
                    </select>
                </div>

                <!-- Payment Information Section -->
                <div class="payment-info">
                    <h3 class="section-title">Payment Information</h3>
                    <div class="payment-methods">
                        <label>
                            <input type="radio" name="payment-method" value="cash"> Cash
                        </label>
                        <label>
                            <input type="radio" name="payment-method" value="credit-card" checked> Credit Card
                        </label>
                        <label>
                            <input type="radio" name="payment-method" value="e-wallet"> E-Wallet
                        </label>
                    </div>
                    <div class="credit-card-info">
                        <input type="text" placeholder="Card Number" class="card-input" />
                        <input type="text" placeholder="Expiration Date" class="card-input" />
                        <input type="text" placeholder="CVV" class="card-input" />
                    </div>
                </div>

                <!-- Promotion Code Section -->
                <div class="promotion">
                    <h3 class="section-title">Promo Code</h3>
                    <input type="text" placeholder="Enter promo code" class="promo-code-input">
                    <button class="apply-promo">Apply</button>
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
                    <button class="place-order">Place Order</button>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="components/footer.jsp" %>
    </footer>
</html>


