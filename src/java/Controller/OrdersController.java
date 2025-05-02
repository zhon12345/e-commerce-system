/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Orders;
import Model.Users;
import static Utils.Authentication.isAuthorized;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
@WebServlet(name = "OrdersController", urlPatterns = { "/user/history", "/admin/orders" })
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
		String path = req.getServletPath();
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (path.equals("/user/history")) {
			if (user == null) {
				res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=history");
				return;
			}

			try {
				String status = req.getParameter("status");

				String jpql = "SELECT DISTINCT o FROM Orders o " +
						"LEFT JOIN FETCH o.orderdetailsList od " +
						"LEFT JOIN FETCH od.productId p " +
						"LEFT JOIN FETCH p.categoryId " +
						"WHERE o.userId.id = :userId";

				if (status != null) {
					jpql += " AND o.status = :status";
				}

				jpql += " ORDER BY o.orderDate DESC";

				TypedQuery<Orders> query = em.createQuery(jpql, Orders.class)
						.setParameter("userId", user.getId());

				if (status != null) {
					query.setParameter("status", status);
				}

				List<Orders> orderList = query.getResultList();

				req.setAttribute("status", status);
				req.setAttribute("orders", orderList);
				req.getRequestDispatcher("/user/history.jsp").forward(req, res);
				return;
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		if (path.equals("/admin/orders")) {
			if (!isAuthorized(user)) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			try {
				String jpql = "SELECT DISTINCT o FROM Orders o " +
						"LEFT JOIN FETCH o.userId " +
						"LEFT JOIN FETCH o.promoId " +
						"LEFT JOIN FETCH o.orderdetailsList od " +
						"LEFT JOIN FETCH od.productId " +
						"ORDER BY o.orderDate DESC";

				List<Orders> ordersList = em.createQuery(jpql, Orders.class).getResultList();

				req.setAttribute("orders", ordersList);
				req.getRequestDispatcher("/admin/admin_orders.jsp").forward(req, res);
				return;
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		res.sendError(HttpServletResponse.SC_NOT_FOUND);
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
		String path = req.getServletPath();

		if (!path.equals("/admin/orders")) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (!isAuthorized(user)) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
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
