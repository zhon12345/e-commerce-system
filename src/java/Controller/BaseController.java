package Controller;

import Model.Users;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;

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
		req.getSession().setAttribute("success", message);
	}

	protected void setErrorMessage(HttpServletRequest req, String message) {
		req.getSession().setAttribute("error", message);
	}
}
