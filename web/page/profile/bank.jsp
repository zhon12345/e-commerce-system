<%--
    Document   : bank
    Created on : 15 Apr 2025, 2:28:26 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bank</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/bank.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/empty_status.css">
    </head>
    <header>
        <%@include file="../../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Bank & Cards</h2>
        </div>
        <div class="container">
            <!-- sidebar -->
            <div class="sidebar">
                <h3>My Account</h3>
                <ul>
                    <li><a href="profile.jsp">Profile</a></li>
                    <li><a href="address.jsp">Address</a></li>
                    <li><a href="bank.jsp" class="active">Bank & Card</a></li>
                    <li><a href="history.jsp">History</a></li>
                </ul>
            </div>

            <!-- content -->
            <div class="content">
                <div class="header">
                    <h2>Credit / Debit Cards</h2>
                    <button class="add-card">
                        <i class="fas fa-plus"></i> Add New Card
                    </button>
                </div>

                <!-- card list sample -->
                <div class="list">
                    <!--
                    <div class="card">
                        <div class="number">•••• •••• •••• 4242</div>
                        <div class="details">
                            <div>
                                <div class="label">Card Holder</div>
                                <div>John Doe</div>
                            </div>
                            <div>
                                <div class="label">Expires</div>
                                <div>12/25</div>
                            </div>
                        </div>
                        <div class="actions">
                            <button class="delete">Delete</button>
                        </div>
                    </div>
                    -->
                </div>

                <!-- empty state sample -->
                <div class="empty-status">
                    <i class="far fa-credit-card"></i>
                    <h3>You don't have cards yet.</h3>
                    <p>Add your credit or debit card to make payments easier.</p>
                </div>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="../../h&f/footer.jsp" %>
    </footer>
</html>