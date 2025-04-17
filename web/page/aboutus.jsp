<%--
    Document   : aboutus
    Created on : 1 Apr 2025, 4:19:48 pm
    Author     : yjee0
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>About Us</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.alsgp0.fds.api.mi-img.com/xiaomi-b2c-i18n-upload/i18n/micon/iconfont.css" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/h&f/title.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/aboutus.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/page/body.css">
    </head>

    <header>
        <%@include file="../h&f/navbar.jsp" %>
    </header>
    <body>
        <!-- title -->
        <div class="title">
            <h2>About Us</h2>
        </div>

        <!-- Our Mission -->
        <div class="container">
            <div class="about">
                <h2>Our Mission</h2>
                <p class="mv">"To provide the latest tech gadgets and accessories that enhance digital lifestyles, offering premium quality at accessible prices."</p>
            </div>

            <div class="divider"></div>

            <!-- Our Vision -->
            <div class="about">
                <h2>Our Vision</h2>
                <p class="mv">"Construct a community of like-minded tech enthusiasts while enriching everyone's digital journey."</p>
            </div>

            <div class="divider"></div>

            <!-- founders -->
            <div class="about">
                <h2>Our Story</h2>
                <p class="mv">"Founded by six friends united by a shared passion for technology.
                    With an unwavering focus on quality and the passion to make advanced tech more accessible."</p>

                <div class="founders">
                    <p>Meet the team behind Giant Rex Tech Store's success:</p>
                    <div class="grid">
                        <div class="card">
                            <div class="avatar">
                                <img src="${pageContext.request.contextPath}/pic/pic_about/nzo.jpg" alt="Founder 1">
                            </div>
                            <h3>Ng Zhun Onn</h3>
                            <p>Co-Founder & CEO</p>
                        </div>
                        <div class="card">
                            <div class="avatar">
                                <img src="${pageContext.request.contextPath}/pic/pic_about/jeremy.png" alt="Founder 2">
                            </div>
                            <h3>Jeremy Lim Jia Xuan</h3>
                            <p>Co-Founder & CTO</p>
                        </div>
                        <div class="card">
                            <div class="avatar">
                                <img src="${pageContext.request.contextPath}/pic/pic_about/kyan.jpg" alt="Founder 3">
                            </div>
                            <h3>Lee Guan Heng</h3>
                            <p>Co-Founder & COO</p>
                        </div>
                        <div class="card">
                            <div class="avatar">
                                <img src="${pageContext.request.contextPath}/pic/pic_about/nene.png" alt="Founder 4">
                            </div>
                            <h3>Yap Sheng Jin</h3>
                            <p>Co-Founder & CFO</p>
                        </div>
                        <div class="card">
                            <div class="avatar">
                                <img src="${pageContext.request.contextPath}/pic/pic_about/yjj.png" alt="Founder 5">
                            </div>
                            <h3>Yeong Jia Jun</h3>
                            <p>Co-Founder & CMO</p>
                        </div>
                        <div class="card">
                            <div class="avatar">
                                <img src="${pageContext.request.contextPath}/pic/pic_about/rex.jpg" alt="Founder 6">
                            </div>
                            <h3>Lee Jee Yang</h3>
                            <p>Co-Founder & CPO</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </body>
    <footer>
        <%@include file="../h&f/footer.jsp" %>
    </footer>
</html>