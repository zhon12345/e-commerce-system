/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.user;

import Model.Orderdetails;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Orders;
import Model.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author zhon12345
 */
public class HistoryController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

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

		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=history");
			return;
		}

		try {
			List<Orders> orders = em.createQuery(
					"SELECT o FROM Orders o WHERE o.userId.id = :userId ORDER BY o.orderDate DESC",
					Orders.class)
					.setParameter("userId", user.getId())
					.getResultList();

			Map<Integer, List<Orderdetails>> orderDetailsMap = new HashMap<>();

			for (Orders order : orders) {
				List<Orderdetails> orderDetails = em.createQuery(
						"SELECT od FROM Orderdetails od LEFT JOIN FETCH od.productId p LEFT JOIN FETCH p.categoryId WHERE od.orderId.id = :orderId",
						Orderdetails.class)
						.setParameter("orderId", order.getId())
						.getResultList();

				orderDetailsMap.put(order.getId(), orderDetails);
			}

			req.setAttribute("orderList", orders);
			req.setAttribute("orderDetailsMap", orderDetailsMap);
			req.getRequestDispatcher("/user/history.jsp").forward(req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
