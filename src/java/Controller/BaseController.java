/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Users;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;

/**
 *
 * @author zhon12345
 */
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
