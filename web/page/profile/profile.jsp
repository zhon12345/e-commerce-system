<%--
    Document   : profile
    Created on : 14 Apr 2025, 7:28:47 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Profile</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/profile.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/popup.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/profile/popup_form.css">
    </head>
    <header>
        <%@include file="../../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Profile</h2>
        </div>

        <div class="container">
            <!-- sidebar -->
            <div class="sidebar">
                <h3>My Account</h3>
                <ul>
                    <li><a href="profile.jsp" class="active">Profile</a></li>
                    <li><a href="address.jsp">Address</a></li>
                    <li><a href="bank.jsp">Bank & Card</a></li>
                    <li><a href="history.jsp">History</a></li>
                </ul>
            </div>

            <!-- content of avatar -->
            <div class="content">
                <div class="header">
                    <div class="avatar">
                        <img src="../../jeramy.jpg" alt="Avatar" class="pic" id="userAvatar">
                        <!-- upload pic -->
                        <div class="upload-pic" title="Change photo">
                            <i class="fas fa-camera"></i>
                            <input type="file" id="avatarInput" accept="image/*" style="display: none;">
                        </div>
                    </div>
                    <div class="Info">
                        <div class="name">Jeramy</div>
                        <button class="edit" id="editProfileBtn">Edit Profile</button>
                    </div>
                </div>

                <!-- content of info -->
                <h2>Personal Information</h2>

                <div class="info">
                    <label>Username:</label>
                    <p id="usernameDisplay">Jeramy</p>
                </div>

                <div class="info">
                    <label>Email:</label>
                    <p id="emailDisplay">jeramy2004@gmail.com</p>
                </div>

                <div class="info">
                    <label>Phone:</label>
                    <p id="phoneDisplay">+60 123456789</p>
                </div>
            </div>
        </div>

        <!-- edit profile -->
        <div class="edit-container" id="editPopup">
            <div class="edit-content">
                <span class="close-btn" id="closePopupBtn">&times;</span>
                <h2>Edit Profile Information</h2>
                <form method="POST" id="profileForm">
                    <div class="edit-info">
                        <label for="editUsername">Username</label>
                        <input type="text" id="editUsername" name="username" value="Jeramy">
                    </div>
                    <div class="edit-info">
                        <label for="editEmail">Email</label>
                        <input type="email" id="editEmail" name="email" value="jeramy2004@gmail.com">
                    </div>
                    <div class="edit-info">
                        <label for="editPhone">Phone</label>
                        <input type="tel" id="editPhone" name="phone" value="+60 123456789">
                    </div>
                    <button type="submit" class="btn">Save</button>
                </form>
            </div>
        </div>

        <%
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");

            if (username != null && !username.isEmpty() &&
                email != null && !email.isEmpty() &&
                phone != null && !phone.isEmpty()) {

                session.setAttribute("editSuccess", "true");
                response.sendRedirect(request.getContextPath() + "/page/profile/profile.jsp");
                return;
            } else {
                return;
            }
        }
        %>

        <!-- success message popup -->
        <% if (session.getAttribute("editSuccess") != null && session.getAttribute("editSuccess").equals("true")) { %>
        <div class="overlay show" id="overlay"></div>
        <div class="popup show" id="popup">
            <div class="success-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <div class="success-title">Edit Successful!</div>
            <button class="close" onclick="closePopup()">OK</button>
            <% session.removeAttribute("editSuccess"); %>
        </div>
        <% } %>

        <script>
            // avatar upload
            document.querySelector('.upload-pic').addEventListener('click', function () {
                document.getElementById('avatarInput').click();
            });

            document.getElementById('avatarInput').addEventListener('change', function (e) {
                if (e.target.files && e.target.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function (event) {
                        document.getElementById('userAvatar').src = event.target.result;
                    }
                    reader.readAsDataURL(e.target.files[0]);
                }
            });

            // edit profile
            const editProfileBtn = document.getElementById('editProfileBtn');
            const editPopup = document.getElementById('editPopup');
            const closePopupBtn = document.getElementById('closePopupBtn');
            const profileForm = document.getElementById('profileForm');

            // open popup form
            editProfileBtn.addEventListener('click', function () {
                editPopup.style.display = 'flex';
                document.getElementById('editUsername').value = document.getElementById('usernameDisplay').textContent;
                document.getElementById('editEmail').value = document.getElementById('emailDisplay').textContent;
                document.getElementById('editPhone').value = document.getElementById('phoneDisplay').textContent;
            });

            // close popup form
            closePopupBtn.addEventListener('click', function () {
                editPopup.style.display = 'none';
            });

            // after edit Successful popup
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