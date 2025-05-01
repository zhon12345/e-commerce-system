/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Model.Reviews;
import Model.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author zhon12345
 */
public class ReviewsController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Check if user is logged in and has appropriate role
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		// If user is not logged in or is not staff/manager, throw a 403 error
		if (user == null || !(user.getRole().equalsIgnoreCase("staff") || user.getRole().equalsIgnoreCase("manager"))) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
			return;
		}

		try {
			// User is authorized, proceed with normal flow
			List<Reviews> ReviewsList = em.createQuery(
					"SELECT r FROM Reviews r LEFT JOIN FETCH r.userId LEFT JOIN FETCH r.productId WHERE r.isArchived = :isArchived",
					Reviews.class)
					.setParameter("isArchived", false)
					.getResultList();

			req.setAttribute("reviews", ReviewsList);
			req.getRequestDispatcher("/admin/admin_reviews.jsp").forward(req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Check if user is logged in and has appropriate role
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		// If user is not logged in or is not staff/manager, throw a 403 error
		if (user == null || !(user.getRole().equalsIgnoreCase("staff") || user.getRole().equalsIgnoreCase("manager"))) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
			return;
		}

		// Continue with normal POST processing
	}
}
