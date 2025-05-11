package Controller;

import Model.Orderdetails;
import Model.Orders;
import Model.Reports;
import Model.Users;
import static Utils.Authentication.isLoggedInAndAuthorized;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "DashboardController", urlPatterns = { "/admin/dashboard" })
public class DashboardController extends BaseController {

	/**
	 *
	 * @param req servlet request
	 * @param res servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Users user = getCurrentUser(req);

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		try {
			int totalUsers = ((Number) em.createQuery(
					"SELECT COUNT(u) FROM Users u WHERE u.isArchived = :archived AND u.role = :role")
					.setParameter("archived", false)
					.setParameter("role", "customer")
					.getSingleResult()).intValue();

			int totalProducts = ((Number) em.createQuery(
					"SELECT COUNT(p) FROM Products p WHERE p.isArchived = :archived")
					.setParameter("archived", false)
					.getSingleResult()).intValue();

			int totalOrders = ((Number) em.createQuery(
					"SELECT COUNT(DISTINCT o.id) FROM Orders o WHERE o.status IN :statuses")
					.setParameter("statuses", Arrays.asList("packaging", "shipping", "delivery"))
					.getSingleResult()).intValue();

			BigDecimal totalRevenue = (BigDecimal) em.createQuery(
					"SELECT COALESCE(SUM(DISTINCT o.totalPrice), 0) FROM Orders o WHERE o.status IN :statuses")
					.setParameter("statuses", Arrays.asList("packaging", "shipping", "delivery"))
					.getSingleResult();

			List<Reports> recentReports = em.createQuery(
					"SELECT r FROM Reports r ORDER BY r.generatedDate DESC", Reports.class)
					.setMaxResults(5)
					.getResultList();

			req.setAttribute("totalUsers", totalUsers);
			req.setAttribute("totalProducts", totalProducts);
			req.setAttribute("totalOrders", totalOrders);
			req.setAttribute("totalRevenue", totalRevenue);
			req.setAttribute("recentReports", recentReports);

			forwardToPage(req, res, "/admin/admin_dashboard.jsp");
		} catch (Exception e) {
			setErrorMessage(req, "Error loading dashboard data: " + e.getMessage());
			redirectToPage(req, res, "/admin/dashboard");
		}
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
		Users user = getCurrentUser(req);

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");

		if (startDateStr == null || startDateStr.isEmpty()) {
			setErrorMessage(req, "Start date is required.");
			redirectToPage(req, res, "/admin/dashboard");
			return;
		}

		if (endDateStr == null || endDateStr.isEmpty()) {
			setErrorMessage(req, "End date is required.");
			redirectToPage(req, res, "/admin/dashboard");
			return;
		}

		Date startDate = null, endDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			startDate = dateFormat.parse(startDateStr);
			endDate = dateFormat.parse(endDateStr);
		} catch (Exception e) {
			setErrorMessage(req, "Error parsing dates: " + e.getMessage());
			redirectToPage(req, res, "/admin/dashboard");
			return;
		}

		List<Orders> orders = em.createQuery(
        "SELECT DISTINCT o FROM Orders o " +
                "LEFT JOIN FETCH o.orderdetailsList " +
                "WHERE o.orderDate BETWEEN :startDate AND :endDate",
        Orders.class)
        .setParameter("startDate", startDate)
        .setParameter("endDate", endDate)
        .getResultList();

		BigDecimal totalRevenue = BigDecimal.ZERO;
		BigDecimal totalDiscounts = BigDecimal.ZERO;
		Set<Integer> uniqueCustomers = new HashSet<>();
		int totalProducts = 0;

		for (Orders order : orders) {
			totalRevenue = totalRevenue.add(order.getTotalPrice());
			if (order.getDiscount() != null) {
				totalDiscounts = totalDiscounts.add(order.getDiscount());
			}
			uniqueCustomers.add(order.getUserId().getId());

			for (Orderdetails detail : order.getOrderdetailsList()) {
				totalProducts += detail.getQuantity();
			}
		}

		final Date startDateFinal = startDate;
		final Date endDateFinal = endDate;
		final BigDecimal totalRevenueFinal = totalRevenue;
		final BigDecimal totalDiscountsFinal = totalDiscounts;
		final int totalProductsFinal = totalProducts;

		BigDecimal averageOrderValue = orders.isEmpty() ? BigDecimal.ZERO
				: totalRevenue.divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP);

		handleTransaction(() -> {
			Reports report = new Reports();
			report.setReportType("Sales Report");
			report.setGeneratedById(user);
			report.setDetails(formatReportDetails(
					startDateFinal, endDateFinal, orders.size(),
					totalRevenueFinal, averageOrderValue,
					totalDiscountsFinal, totalProductsFinal,
					uniqueCustomers.size()));

			em.persist(report);
		}, req, "Report generated successfully!", "Error generating report");

		redirectToPage(req, res, "/admin/dashboard");
	}

	private String formatReportDetails(Date startDate, Date endDate, int totalOrders,
			BigDecimal totalRevenue, BigDecimal averageOrderValue,
			BigDecimal totalDiscounts, int totalProducts, int uniqueCustomers) {
		return String.format(
				"Period: %s to %s\n" +
						"Total Orders: %d\n" +
						"Total Revenue: RM %.2f\n" +
						"Average Order Value: RM %.2f\n" +
						"Total Discounts: RM %.2f\n" +
						"Total Products Sold: %d\n" +
						"Unique Customers: %d",
				new SimpleDateFormat("yyyy-MM-dd").format(startDate),
				new SimpleDateFormat("yyyy-MM-dd").format(endDate),
				totalOrders, totalRevenue, averageOrderValue,
				totalDiscounts, totalProducts, uniqueCustomers);
	}
}