<%--
    Document   : error
    Created on : Apr 29, 2025, 3:37:11 PM
    Author     : zhon12345
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Error - ${pageContext.errorData.statusCode}</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/components/title.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pages/error.css">
</head>
<header>
	<%@include file="../../components/navbar.jsp" %>
</header>

<body>
	<div class="title">
		<h2>Oops! Something went wrong</h2>
	</div>

	<div class="container">
		<div class="error-container">
			<h1 class="error-code">
				<i class="fas fa-exclamation-circle"></i>
				Error ${pageContext.errorData.statusCode}
			</h1>
			<h2>
				<%
					String errorTitle;
					int statusCode = pageContext.getErrorData().getStatusCode();

					switch(statusCode) {
						case 403:
							errorTitle = "Access Denied";
							break;
						case 404:
							errorTitle = "Page Not Found";
							break;
						case 405:
						 errorTitle = "Method Not Allowed";
						 	break;
						case 500:
							errorTitle = "Internal Server Error";
							break;
						default:
							errorTitle = "Something Went Wrong";
					}
					out.print(errorTitle);
				%>
			</h2>

			<p>
				<%
					String errorMessage;

					switch(statusCode) {
						case 403:
							errorMessage = "You don't have permission to access this resource. Please contact us if this is an error.";
							break;
						case 404:
							errorMessage = "The page you're looking for doesn't exist or may have been moved.";
							break;
						 case 405:
            	errorMessage = "The HTTP method used is not supported for this resource.";
            	break;
						case 500:
							errorMessage = "Our server encountered an error while processing your request. We have been notified and is working on it.";
							break;
						default:
							errorMessage = "An unexpected error occurred while processing your request. Please try again later.";
					}
					out.print(errorMessage);
				%>
			</p>

			<div class="button-group">
				<a href="${pageContext.request.contextPath}/index" class="btn btn-primary">
					<i class="fas fa-home"></i> Return Home
				</a>
				<button onclick="history.back()" class="btn btn-outline-secondary">
					<i class="fas fa-arrow-left"></i> Go Back
				</button>
			</div>

			<% if (exception != null) { %>
			<div class="error-details">
				<details>
					<summary><i class="fas fa-code"></i> Technical Details (for developers)</summary>
					<div>
						<p><strong>Error Type:</strong> <%= exception.getClass().getName() %></p>
						<p><strong>Message:</strong> <%= exception.getMessage() %></p>
						<div>
							<pre style="white-space: pre-wrap;"><%
								for (StackTraceElement element : exception.getStackTrace()) {
									out.println(element.toString());
								}
							%></pre>
						</div>
					</div>
				</details>
			</div>
			<% } %>
		</div>
	</div>
</body>
<footer>
	<%@include file="../../components/footer.jsp" %>
</footer>

</html>