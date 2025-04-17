<%--
    Document   : history
    Created on : 15 Apr 2025, 11:01:40 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Order History</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/history.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/empty_status.css">
    </head>
    <header>
        <%@include file="../../h&f/navbar.jsp"%>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Order History</h2>
        </div>

        <div class="container">
            <!-- Sidebar Navigation -->
            <div class="sidebar">
                <h3>My Account</h3>
                <ul>
                    <li><a href="profile.jsp">Profile</a></li>
                    <li><a href="address.jsp">Address</a></li>
                    <li><a href="bank.jsp">Bank & Card</a></li>
                    <li><a href="history.jsp" class="active">Order History</a></li>
                </ul>
            </div>

            <!-- content -->
            <div class="content">
                <div class="header-status">
                    <h2>My Orders</h2>
                    <div class="filter">
                        <select>
                            <option>All Status</option>
                            <option>Completed</option>
                            <option>Pending</option>
                            <option>Cancelled</option>
                        </select>
                    </div>
                </div>

                <!-- order display sample -->
                <!--
                <div class="card">
                    <div class="header">
                        <div>
                            <span class="id">Order #12345</span>
                            <span class="date">• 12 April 2025</span>
                        </div>
                        <span class="status status-completed">Completed</span>
                    </div>

                    <div class="details">
                        <img src="pic/jeramy.jpeg" alt="Product Image" class="image">
                        <div class="info">
                            <div class="name">Wireless Bluetooth Headphones</div>
                            <div class="price">$99.99 × 1</div>
                        </div>
                    </div>

                    <div class="actions">
                        <button class="btn btn-primary">Buy Again</button>
                    </div>
                </div>
                -->

                <!-- empty state sample -->
                <div class="empty-status">
                    <h3>No orders yet</h3>
                    <p>You haven't placed any orders. Start shopping now!</p>
                    <button class="btn btn-primary" style="margin-top: 15px;">Shop Now</button>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="../../h&f/footer.jsp"%>
    </footer>
</html>