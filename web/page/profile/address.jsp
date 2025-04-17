<%--
    Document   : address
    Created on : 15 Apr 2025, 1:32:39 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Address</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/address.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/empty_status.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/popup.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/popup_form.css">
    </head>
    <header>
        <%@include file="../../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Address</h2>
        </div>

        <div class="container">
            <!-- sidebar  -->
            <div class="sidebar">
                <h3>My Account</h3>
                <ul>
                    <li><a href="profile.jsp">Profile</a></li>
                    <li><a href="address.jsp" class="active">Address</a></li>
                    <li><a href="bank.jsp">Bank & Card</a></li>
                    <li><a href="history.jsp">History</a></li>
                </ul>
            </div>

            <!-- content -->
            <div class="content">
                <div class="header">
                    <h2>My Address</h2>
                    <button class="add" id="addAddressBtn">Add New Address</button>
                </div>

                <!-- address display sample -->
                <div class="card">
                    <div class="card-left">
                        <p>Jeramy</p>
                        <p>+60 123456789</p>
                        <p>123 Main Street</p>
                        <p>Apartment 4B</p>
                        <p>New York, NY 10001</p>
                        <p>United States</p>
                    </div>
                    <div class="card-right">
                        <button class="edit-btn" onclick="openEditPopup()">Edit</button>
                        <button class="delete-btn" onclick="deleteAddress()">Delete</button>
                    </div>
                </div>

                <!--
                <div class="empty-status">
                    <i class="fas fa-map-marker-alt"></i>
                    <h3>No Saved Addresses</h3>
                    <p>You haven't added any addresses yet. Add your first address to get started.</p>
                </div>
                -->
            </div>
        </div>

        <!-- edit profile -->
        <div class="add-container" id="addPopup">
            <div class="add-content">
                <span class="close-btn" id="closePopupBtn">&times;</span>
                <h2>Add New Address</h2>
                <form method="POST" id="newAddressForm">
                    <div class="add-info">
                        <label for="addUsername">Username</label>
                        <input type="text" id="addUsername" name="username" required>
                    </div>
                    <div class="add-info">
                        <label for="addPhone">Phone</label>
                        <input type="tel" id="editPhone" name="phone" required>
                    </div>
                    <div class="add-info">
                        <label for="addLine1">Address Line 1</label>
                        <input type="text" id="addLine1" name="line1" required>
                    </div>
                    <div class="add-info">
                        <label for="addLine2">Address Line 2(Optional)</label>
                        <input type="text" id="addLine2" name="line2">
                    </div>
                    <div class="add-info">
                        <label for="addPostCode">Postal Code / City</label>
                        <input type="text" id="addPostCode" name="postcode" required>
                    </div>
                    <div class="add-info">
                        <label for="addState">State / Country</label>
                        <input type="text" id="addState" name="state" required>
                    </div>
                    <button type="submit" class="btn">Add</button>
                </form>
            </div>
        </div>

        <%
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String username = request.getParameter("username");
            String phone = request.getParameter("phone");
            String line1 = request.getParameter("line1");
            String line2 = request.getParameter("line2");
            String postcode = request.getParameter("postcode");
            String state = request.getParameter("state");

            if (username != null && !username.isEmpty() &&
                phone != null && !phone.isEmpty() &&
                line1 != null && !line1.isEmpty() &&
                line2 != null &&
                postcode != null && !postcode.isEmpty() &&
                state != null && !state.isEmpty() ) {

                session.setAttribute("addSuccess", "true");
                response.sendRedirect(request.getContextPath() + "/page/profile/address.jsp");
                return;
            } else {
                return;
            }
        }
        %>

        <!-- success message popup -->
        <% if (session.getAttribute("addSuccess") != null && session.getAttribute("addSuccess").equals("true")) { %>
        <div class="overlay show" id="overlay"></div>
        <div class="popup show" id="popup">
            <div class="success-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <div class="success-title">Add Successful!</div>
            <button class="close" onclick="closePopup()">OK</button>
            <% session.removeAttribute("addSuccess"); %>
        </div>
        <% } %>

        <script>
            // add address
            const addAddressBtn = document.getElementById('addAddressBtn');
            const addPopup = document.getElementById('addPopup');
            const closePopupBtn = document.getElementById('closePopupBtn');
            const newAddressForm = document.getElementById('newAddressForm');

            // open popup form
            addAddressBtn.addEventListener('click', function () {
                addPopup.style.display = 'flex';
                document.getElementById('addUsername').value = document.getElementById('usernameDisplay').textContent;
                document.getElementById('addPhone').value = document.getElementById('phoneDisplay').textContent;
                document.getElementById('addLine1').value = document.getElementById('line1Display').textContent;
                document.getElementById('addLine2').value = document.getElementById('line2Display').textContent;
                document.getElementById('addPostCode').value = document.getElementById('postcodeDisplay').textContent;
                document.getElementById('addState').value = document.getElementById('stateDisplay').textContent;
            });

            // close popup form
            closePopupBtn.addEventListener('click', function () {
                addPopup.style.display = 'none';
            });

            // after add Successful popup
            function closePopup() {
                const popup = document.getElementById('popup');
                const overlay = document.getElementById('overlay');

                if (popup && overlay) {
                    popup.classList.remove('show');
                    overlay.classList.remove('show');
                }
            }
        </script>
    </body>
    <footer>
        <%@include file="../../h&f/footer.jsp" %>
    </footer>
</html>
