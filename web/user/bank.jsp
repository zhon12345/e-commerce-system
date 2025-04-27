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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/bank.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/popup_form.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/empty_status.css">
    </head>
    <header>
        <%@include file="../components/navbar.jsp" %>
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
                    <li><a href="address">Address</a></li>
                    <li><a href="bank.jsp" class="active">Bank & Card</a></li>
                    <li><a href="history.jsp">History</a></li>
                </ul>
            </div>

            <!-- content -->
            <div class="content">
                <div class="header">
                    <h2>Credit / Debit Cards</h2>
                    <button class="add-card" id="addBankBtn">
                        <i class="fas fa-plus"></i> Add New Card
                    </button>
                </div>

                <!-- card list sample -->
                <div class="list">
                    <div class="card">
                        <div class="number">•••• •••• •••• 4242</div>
                        <div class="details">
                            <div>
                                <div class="label">Card Holder</div>
                                <div>John Doe</div>
                            </div>
                            <div>
                                <div class="label">Expiry Date</div>
                                <div>12/25</div>
                            </div>
                    <div>
                                <div class="label">Cvv</div>
                                <div>123</div>
                            </div>
                        </div>
                        <div class="actions">
                            <button class="delete">Delete</button>
                        </div>
                    </div>
                </div>

                <!-- empty state sample -->
                <!--
                <div class="empty-status">
                    <i class="far fa-credit-card"></i>
                    <h3>You don't have cards yet.</h3>
                    <p>Add your credit or debit card to make payments easier.</p>
                </div>
                -->
            </div>
        </div>

        <!-- add bank -->
        <div class="add-container" id="addPopup" style="display: none;">
            <div class="add-content">
                <span class="close-btn" id="closePopupBtn">&times;</span>
                <h2>Add New Bank Account</h2>
                <form method="POST" id="newBankForm">
                    <div class="add-info">
                        <label for="bankName">Bank Name</label>
                        <input type="text" id="bankName" name="bankName" required>
                    </div>
                    <div class="add-info">
                        <label for="accountNumber">Account Number</label>
                        <input type="text" id="accountNumber" name="accountNumber" required>
                    </div>
                    <div class="add-info">
                        <label for="routingNumber">Expiry Date</label>
                        <input type="text" id="expiryDate" name="expiryDate" required>
                    </div>
                    <div class="add-info">
                        <label for="cvv">cvv</label>
                        <input type="text" id="cvv" name="cvv" required>
                    </div>
                    <button type="submit" class="btn">Add Bank Account</button>
                </form>
            </div>
        </div>

        <%
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String bankName = request.getParameter("bankName");
            String accountNumber = request.getParameter("accountNumber");
            String cvv = request.getParameter("cvv");
            String expiryDate = request.getParameter("expiryDate");

            if (bankName != null && !bankName.isEmpty() &&
                accountNumber != null && !accountNumber.isEmpty() &&
                cvv != null && !cvv.isEmpty() &&
                expiryDate != null && !expiryDate.isEmpty()) {

                session.setAttribute("addSuccess", "true");
                response.sendRedirect(request.getContextPath() + "/user/bank.jsp");
                return;
            } else {
                return;
            }
        }
        %>

        <script>
            // Function to disable scrolling
            function disableScroll() {
                const scrollY = window.scrollY || document.documentElement.scrollTop;

                document.body.style.position = 'fixed';
                document.body.style.top = `-${scrollY}px`;
                document.body.style.width = '100%';
                document.body.style.overflow = 'hidden';
            }

            // Function to enable scrolling
            function enableScroll() {
                const scrollY = parseInt(document.body.style.top || '0');

                document.body.style.position = '';
                document.body.style.top = '';
                document.body.style.width = '';
                document.body.style.overflow = '';

                window.scrollTo(0, Math.abs(scrollY));
            }

            // Add bank account form
            const addBankBtn = document.getElementById('addBankBtn');
            const addPopup = document.getElementById('addPopup');
            const closePopupBtn = document.getElementById('closePopupBtn');

            // open popup form
            addBankBtn.addEventListener('click', function () {
                addPopup.style.display = 'flex';
                disableScroll();
            });

            // close popup form
            closePopupBtn.addEventListener('click', function () {
                addPopup.style.display = 'none';
                enableScroll();
            });
        </script>
    </body>
    <footer>
        <%@include file="../components/footer.jsp" %>
    </footer>
</html>