package Controller;

import Model.Users;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseController extends HttpServlet {

	private static final Logger logger = Logger.getLogger(BaseController.class.getName());

	@PersistenceContext
	protected EntityManager em;

	@Resource
	protected UserTransaction utx;

	protected Users getCurrentUser(HttpServletRequest req) {
		HttpSession session = req.getSession();
		return (Users) session.getAttribute("user");
	}

	protected void setSuccessMessage(HttpServletRequest req, String message) {
		req.getSession().setAttribute("success", message);
	}

	protected void setErrorMessage(HttpServletRequest req, String message) {
		req.getSession().setAttribute("error", message);
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
				logger.log(Level.SEVERE, "Transaction rollback failed", ex);
			}

			logger.log(Level.SEVERE, errorMsg + ": " + e.getMessage(), e);
			setErrorMessage(req, errorMsg + ": " + e.getMessage());
		}
	}

	protected void forwardToPage(HttpServletRequest req, HttpServletResponse res, String page) throws ServletException, IOException {
		req.getRequestDispatcher(page).forward(req, res);
	}

	protected void redirectToPage(HttpServletRequest req, HttpServletResponse res, String path) throws IOException {
		res.sendRedirect(req.getContextPath() + path);
	}
}
