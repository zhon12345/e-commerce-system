package Controller;

import Model.Orders;
import Model.Users;
import static Utils.Authentication.isLoggedIn;
import static Utils.Authentication.isLoggedInAndAuthorized;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrdersController", urlPatterns = { "/user/history", "/admin/orders" })
public class OrdersController extends BaseController {

	/**
	 *
	 * @param req servlet request
	 * @param res servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();
		Users user = getCurrentUser(req);

		if (path.equals("/user/history")) {
			if (!isLoggedIn(req, res, user, "history")) return;

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
				forwardToPage(req, res, "/user/history.jsp");
				return;
			} catch (Exception e) {
				handleException(req, res, e);
			}
		}

		if (path.equals("/admin/orders")) {
			if (!isLoggedInAndAuthorized(req, res, user, null)) return;

			try {
				String jpql = "SELECT DISTINCT o FROM Orders o " +
						"LEFT JOIN FETCH o.userId " +
						"LEFT JOIN FETCH o.promoId " +
						"LEFT JOIN FETCH o.orderdetailsList od " +
						"LEFT JOIN FETCH od.productId " +
						"ORDER BY o.orderDate DESC";

				List<Orders> ordersList = em.createQuery(jpql, Orders.class).getResultList();

				req.setAttribute("orders", ordersList);
				forwardToPage(req, res, "/admin/admin_orders.jsp");
				return;
			} catch (Exception e) {
				handleException(req, res, e);
			}
		}

		sendError(req, res, HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 *
	 * @param req servlet request
	 * @param res servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();

		if (!path.equals("/admin/orders")) {
			sendError(req, res, HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Users user = getCurrentUser(req);

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		try {
			int orderId = Integer.parseInt(req.getParameter("orderId"));
			String status = req.getParameter("status");

			Orders order = em.find(Orders.class, orderId);

			if (order == null) {
				setErrorMessage(req, "Order not found");
				return;
			}

			handleTransaction(() -> {
				order.setStatus(status);
				em.merge(order);
			}, req, "Order status updated successfully!", "Failed to update order");

		} catch (NumberFormatException e) {
			handleException(req, e, "Invalid order ID.");
		} catch (Exception e) {
			handleException(req, res, e);
			return;
		}

		redirectToPage(req, res, "/admin/orders");
	}
}
