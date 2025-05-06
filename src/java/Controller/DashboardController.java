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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Users user = getCurrentUser(request);

            if (!isLoggedInAndAuthorized(request, response, user, null)) return;

            String action = request.getParameter("action");

            if ("generate".equals(action)) {
                generateReport(request, response);
                return;
            }

            utx.begin();

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

            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("recentReports", recentReports);

            utx.commit();

            request.getRequestDispatcher("/admin/admin_dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            try {
                if (utx != null) {
                    utx.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Error in AdminDashboard: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/admin/admin_dashboard.jsp").forward(request, response);
        }
    }

    private void generateReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            utx.begin();
            Users user = (Users) request.getSession().getAttribute("user");

            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            List<Orders> orders = em.createQuery(
                    "SELECT DISTINCT o FROM Orders o " +
                            "WHERE o.orderDate BETWEEN :startDate AND :endDate",
                    Orders.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

            for (Orders order : orders) {
                em.refresh(order);
                if (order.getOrderdetailsList() != null) {
                    order.getOrderdetailsList().size();
                }
            }

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

            BigDecimal averageOrderValue = orders.isEmpty() ? BigDecimal.ZERO
                    : totalRevenue.divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP);

            Reports report = new Reports();
            report.setReportType("Sales Report");
            report.setGeneratedDate(new Date());
            report.setGeneratedById(user);
            report.setDetails(formatReportDetails(
                    startDate, endDate, orders.size(),
                    totalRevenue, averageOrderValue,
                    totalDiscounts, totalProducts,
                    uniqueCustomers.size()));

            em.persist(report);
            utx.commit();

            setSuccessMessage(request, "Report generated successfully!");

        } catch (Exception e) {
            handleError(e, request, response);
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
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

    private void handleError(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (utx != null) {
                utx.rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.printStackTrace();
        setErrorMessage(request, "Error: " + e.getMessage());
        request.getRequestDispatcher("/admin/admin_dashboard.jsp").forward(request, response);
    }
}