/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Model.Orders;
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
import java.util.List;

/**
 *
 * @author zhon12345
 */
public class OrdersController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Resource
	private UserTransaction utx;

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
		try {
			// Check if user is logged in and has appropriate role
			HttpSession session = req.getSession();
			Users user = (Users) session.getAttribute("user");

			// If user is not logged in or is not staff/manager, throw a 403 error
			if (user == null || !(user.getRole().equalsIgnoreCase("staff") || user.getRole().equalsIgnoreCase("manager"))) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
				return;
			}

			// First fetch all orders with their basic info without orderdetails to avoid
			// duplication
			List<Orders> ordersList = em.createQuery(
					"SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.userId LEFT JOIN FETCH o.promoId LEFT JOIN FETCH o.orderdetailsList od LEFT JOIN FETCH od.productId ORDER BY o.orderDate DESC",
					Orders.class)
					.getResultList();

			req.setAttribute("orders", ordersList);
			req.getRequestDispatcher("/admin/admin_orders.jsp").forward(req, res);
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
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		// If user is not logged in or is not staff/manager, throw a 403 error
		if (user == null || !(user.getRole().equalsIgnoreCase("staff") || user.getRole().equalsIgnoreCase("manager"))) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
			return;
		}

		try {
			int orderId = Integer.parseInt(req.getParameter("orderId"));
			String status = req.getParameter("status");

			utx.begin();

			Orders order = em.find(Orders.class, orderId);
			if (order != null) {
				order.setStatus(status);
				em.merge(order);
			}

			utx.commit();

			session.setAttribute("updateSuccess", "true");
			res.sendRedirect(req.getContextPath() + "/admin/orders");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			session.setAttribute("error", "Failed to update order: " + e.getMessage());
			res.sendRedirect(req.getContextPath() + "/admin/orders");
		}
	}
}
