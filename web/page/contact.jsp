<%--
    Document   : contact
    Created on : 2 Apr 2025, 8:58:19 am
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Contact Page</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/contact.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
    </head>
    <header>
        <%@include file="../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>Contact</h2>
        </div>

        <!-- form -->
        <div class="container">
            <div class="form">
                <h3>Contact Form</h3>
                <form action="#" method="POST">
                    <div class="text">
                        <label for="name">Name</label>
                        <input type="text" id="name" name="name" required>
                    </div>
                    <div class="text">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required>
                    </div>
                    <div class="text">
                        <label for="subject">Subject</label>
                        <input type="text" id="subject" name="subject" required>
                    </div>
                    <div class="text">
                        <label for="message">Message</label>
                        <textarea id="message" name="message" required></textarea>
                    </div>
                    <button type="submit" class="send">Send Message</button>
                </form>
            </div>


            <!--Maps-->
            <div class="map">
                <iframe
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3983.8000000000006!2d101.7239822!3d3.2152552!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31cc3843bfb6a031%3A0x2dc5e067aae3ab84!2sTunku%20Abdul%20Rahman%20University%20of%20Management%20and%20Technology!5e0!3m2!1sen!2smy!4v1710000000000!5m2!1sen!2smy">
                </iframe>
            </div>
        </div>

        <!--Info-->
        <div class="info">
            <div class="card">
                <i class="fas fa-map-marker-alt"></i>
                <h4>Address</h4>
                <p>Ground Floor, Bangunan Tan Sri Khaw Kai Boh (Block A),<br>Jalan Genting Kelang, Setapak
                    <br>53300 Kuala Lumpur,<br>Federal Territory of Kuala Lumpur</p>
            </div>
            <div class="card">
                <i class="fas fa-phone"></i>
                <h4>Phone</h4>
                <p>+60 122511060<br>+60 194001257</p>
            </div>
            <div class="card">
                <i class="fas fa-envelope"></i>
                <h4>Email</h4>
                <p>jxlim-wm23@student.tarc.edu.my<br>giantRexTechStore@gmail.com</p>
            </div>
            <div class="card">
                <i class="fas fa-clock"></i>
                <h4>Business Hours</h4>
                <p>Mon-Fri: 10:00 AM - 10:00 PM<br>Sat: 10:00 AM - 6:00 PM<br>Sun: Closed</p>
            </div>
        </div>
    </body>
    <footer>
        <%@include file="../h&f/footer.jsp" %>
    </footer>
</html>