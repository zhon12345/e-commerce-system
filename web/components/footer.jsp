<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/footer.css">
        <title>footer</title>
    </head>

    <body>
        <div class="footer">
            <div class="footer_content">
                <div class="column">
                    <div class="logo_name">
                        <img src="${pageContext.request.contextPath}/assets/logo/text_1.png" class="logo">
                    </div>
                    <p>Founded by six friends united by a shared passion for technology.
                        With an unwavering focus on quality and the passion to make advanced tech more accessible.</p>
                </div>

                <div class="column">
                    <h3>Shop</h3>
                    <a href="#"><p>IEM</p></a>
                    <a href="#"><p>Mouse</p></a>
                    <a href="#"><p>Keyboard</p></a>
                </div>

                <div class="column">
                    <h3>Quick Links</h3>
                    <a href="${pageContext.request.contextPath}/index"><p>Home</p></a>
                    <a href="${pageContext.request.contextPath}/products"><p>Shop</p></a>
                    <a href="${pageContext.request.contextPath}/about.jsp"><p>About</p></a>
                    <a href="${pageContext.request.contextPath}/contact.jsp"><p>Contact</p></a>
                </div>

                <div class="column">
                    <h3>Contact</h3>
                    <p>Address: Ground Floor, Bangunan Tan Sri Khaw Kai Boh (Block A),
                        Jalan Genting Kelang,
                        Setapak 53300 Kuala Lumpur,
                        Federal Territory of Kuala Lumpur</p>
                    <br>
                    <p>Email: jxlim-wm23@student.tarc.edu.my</p>
                    <p>Email: giantRexTechStore@gmail.com</p>
                    <br>
                    <p>Phone: +60 122511060</p>
                    <p>Phone: +60 194001257</p>
                </div>
            </div>
            <div class="tag">
                <p>&copy; 2025 - <%= java.time.Year.now().getValue()%> Ng Zhun Onn - All Rights Reserved.</p>
            </div>
        </div>
    </body>
</html>