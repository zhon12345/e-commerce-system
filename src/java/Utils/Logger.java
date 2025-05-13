package Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;

public class Logger {
	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());

	public static void sendError(HttpServletRequest req, HttpServletResponse res, int statusCode, boolean logoutUser) throws IOException {
		logger.log(Level.WARNING, "Returning HTTP error {0} for path {1}",
				new Object[] { statusCode, req.getRequestURI() });

		if (logoutUser) {
			Authentication.logoutUser(req);
		}

		res.sendError(statusCode);
	}

	public static void sendError(HttpServletRequest req, HttpServletResponse res, int statusCode) throws IOException {
		sendError(req, res, statusCode, false);
	}

	public static void handleException(HttpServletRequest req, HttpServletResponse res, Exception e) throws IOException {
		logger.log(Level.SEVERE, "Exception while processing " + req.getRequestURI(), e);

		req.setAttribute("javax.servlet.error.exception", e);

		res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	public static void handleException(HttpServletRequest req, Exception e, String errorMsg) {
		logger.log(Level.SEVERE, "Exeception while processing " + req.getRequestURI(), e);

		if (errorMsg != null && !errorMsg.isEmpty()) {
			setErrorMessage(req, errorMsg);
		} else {
			setErrorMessage(req, "An unexpected error occurred. Please try again later.");
		}
	}

	public static void setSuccessMessage(HttpServletRequest req, String message) {
		req.getSession().setAttribute("success", message);
	}

	public static void setErrorMessage(HttpServletRequest req, String message) {
		req.getSession().setAttribute("error", message);
	}

	public static void info(String message) {
		logger.log(Level.INFO, message);
	}

	public static void warning(String message) {
		logger.log(Level.WARNING, message);
	}

	public static void error(String message) {
		logger.log(Level.SEVERE, message);
	}

	public static void error(String message, Exception e) {
		logger.log(Level.SEVERE, message, e);
	}
}
