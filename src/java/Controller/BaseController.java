package Controller;

import Model.Users;
import Utils.Logger;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import java.io.IOException;

public class BaseController extends HttpServlet {
	@PersistenceContext
	protected EntityManager em;

	@Resource
	protected UserTransaction utx;

	protected Users getCurrentUser(HttpServletRequest req) {
		HttpSession session = req.getSession();
		return (Users) session.getAttribute("user");
	}

	protected void setSuccessMessage(HttpServletRequest req, String message) {
		Logger.setSuccessMessage(req, message);
	}

	protected void setErrorMessage(HttpServletRequest req, String message) {
		Logger.setErrorMessage(req, message);
	}

	protected void handleTransaction(Runnable action, HttpServletRequest req, String successMsg, String errorMsg) {
		try {
			utx.begin();
			action.run();
			utx.commit();

			if (successMsg != null && req.getSession().getAttribute("error") == null) {
				setSuccessMessage(req, successMsg);
			}
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				Logger.error("Transaction rollback failed", ex);
			}

			handleException(req, e, errorMsg);
		}
	}

	protected void sendError(HttpServletRequest req, HttpServletResponse res, int statusCode, boolean logoutUser) throws IOException {
		Logger.sendError(req, res, statusCode, logoutUser);
	}

	protected void sendError(HttpServletRequest req, HttpServletResponse res, int statusCode) throws IOException {
		Logger.sendError(req, res, statusCode);
	}

	protected void handleException(HttpServletRequest req, HttpServletResponse res, Exception e) throws IOException {
		Logger.handleException(req, res, e);
	}

	protected void handleException(HttpServletRequest req, Exception e, String errorMsg) {
		Logger.handleException(req, e, errorMsg);
	}

	protected void forwardToPage(HttpServletRequest req, HttpServletResponse res, String page) throws ServletException, IOException {
		req.getRequestDispatcher(page).forward(req, res);
	}

	protected void redirectToPage(HttpServletRequest req, HttpServletResponse res, String path) throws IOException {
		res.sendRedirect(req.getContextPath() + path);
	}
}
