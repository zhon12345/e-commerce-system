<%--
    Document   : bank
    Created on : 15 Apr 2025, 2:28:26 pm
    Author     : yjee0
--%>

<%@page import="java.util.List, Model.Cardinfo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bank & Cards</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/bank.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/empty_status.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/popup.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/user/popup_form.css">
    </head>
    <header>
        <%@include file="../components/navbar.jsp" %>
    </header>
    <body>
        <!-- Success message popup -->
        <% if (session.getAttribute("addSuccess") != null || session.getAttribute("editSuccess") != null) { %>
        <div class="overlay show" id="overlay"></div>
        <div class="popup show" id="popup">
            <div class="success-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <div class="success-title"><%= session.getAttribute("editSuccess") != null ? "Edit" : "Add" %> Successful!</div>
            <button class="close" onclick="closePopup()">OK</button>
            <%
                session.removeAttribute("addSuccess");
    session.removeAttribute("editSuccess");
            %>
        </div>
        <% } %>

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
                    <li><a href="card" class="active">Bank & Card</a></li>
                    <li><a href="history.jsp">History</a></li>
                </ul>
            </div>

            <!-- content -->
            <div class="content">
                <div class="header">
                    <h2>Credit / Debit Cards</h2>
                    <button class="add-card" id="addCardBtn">
                        <i class="fas fa-plus"></i> Add New Card
                    </button>
                </div>


                <!-- In the card list section -->
                <div class="list">
                    <%
                        List<Cardinfo> cards = (List<Cardinfo>) request.getAttribute("card");
                        if (cards != null && !cards.isEmpty()) {
                            for (Cardinfo card : cards) {
            String cardNumber = "•••• •••• •••• " + card.getCardNumber().substring(card.getCardNumber().length() - 4);
            String expiryDate = new java.text.SimpleDateFormat("MM/yy").format(card.getExpirationDate());
                    %>
                    <div class="card">
                        <div class="number"><%= cardNumber %></div>
                        <div class="details">
                            <div>
                                <div class="label">Card Holder</div>
                                <div><%= card.getCardHolderName() %></div>
                            </div>
                            <div>
                                <div class="label">Expiry Date</div>
                                <div><%= expiryDate %></div>
                            </div>
                            <div>
                                <div class="label">CVV</div>
                                <div><%= card.getCvv() %></div>
                            </div>
                        </div>
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/user/card?action=edit&id=<%= card.getId() %>" class="edit">Edit</a>
                            <a href="${pageContext.request.contextPath}/user/card?action=delete&id=<%= card.getId() %>" class="delete">Delete</a>
                        </div>
                    </div>
                    <%
                            }
                        } else {
                    %>
                    <div class="empty-status">
                        <i class="far fa-credit-card"></i>
                        <h3>No Saved Cards</h3>
                        <p>You haven't added any credit or debit cards yet. Add your first card to get started.</p>
                    </div>
                    <%
                        }
                    %>
                </div>

            </div>
        </div>

        <!-- Add Card Form -->
        <div class="add-container" id="addPopup">
            <div class="add-content">
                <span class="close-btn" id="closePopupBtn">&times;</span>
                <h2>Add New Card</h2>
                <form action="${pageContext.request.contextPath}/user/card" method="POST">
                    <div class="add-info">
                        <label for="number">Card Number</label>
                        <input type="text" id="number" name="number" value="${number}" required>
                        <span class="error-message">${numberError}</span>
                    </div>
                    <div class="add-info">
                        <label for="name">Card Holder Name</label>
                        <input type="text" id="name" name="name" value="${name}" required>
                        <span class="error-message">${nameError}</span>
                    </div>
                    <div class="add-info">
                        <label for="expiryDate">Expiry Date (MM/YY)</label>
                        <input type="text" id="expiryDate" name="expiryDate"
                               placeholder="MM/YY" value="${expiryDate}" required>
                        <span class="error-message">${expiryDateError}</span>
                    </div>
                    <div class="add-info">
                        <label for="cvv">CVV</label>
                        <input type="text" id="cvv" name="cvv" value="${cvv}" required>
                        <span class="error-message">${cvvError}</span>
                    </div>
                    <button type="submit" class="btn">Add Card</button>
                </form>
            </div>
        </div>

                        <script>
            window.addEventListener("DOMContentLoaded", () => {
                            <%
                                                                 String[] errorFields = {"number", "name", "expiryDate", "cvv"};
                                                                 for (String field : errorFields) {
                                                                         String error = (String) request.getAttribute(field + "Error");
                                                                         if (error != null) {
                            %>
                addPopup.style.display = 'flex';
                showError('<%= field %>', '<%= error %>');
                            <%
                                         }
                                 }

         if (request.getAttribute("editEdit") != null) {
            %>
                addPopup.style.display = 'flex';
            <%
                                 }
            %>

            });
        </script>
        <script src="${pageContext.request.contextPath}/scripts/components/popup.js"></script>
    </body>
    <footer>
        <%@include file="../components/footer.jsp" %>
    </footer>
    <script src="${pageContext.request.contextPath}/scripts/user/card.js" type="module"></script>
</html>