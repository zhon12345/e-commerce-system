package Controller.admin;

import Model.*;
import java.io.IOException;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.text.SimpleDateFormat;

public class AdminDashboard extends HttpServlet {
    
    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private UserTransaction utx;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            Users user = (Users) request.getSession().getAttribute("user");
            
            if ("generate".equals(action)) {
                generateReport(request, response);
                return;
            }
            
            utx.begin();
            
            // Get total customers
            int totalUsers = ((Number) em.createQuery(
                "SELECT COUNT(u) FROM Users u WHERE u.isArchived = :archived AND u.role = :role")
                .setParameter("archived", false)
                .setParameter("role", "customer")
                .getSingleResult()).intValue();
            
            // Get total products
            int totalProducts = ((Number) em.createQuery(
                "SELECT COUNT(p) FROM Products p WHERE p.isArchived = :archived")
                .setParameter("archived", false)
                .getSingleResult()).intValue();
            
            // Get total active orders - using DISTINCT
            int totalOrders = ((Number) em.createQuery(
                "SELECT COUNT(DISTINCT o.id) FROM Orders o WHERE o.status IN :statuses")
                .setParameter("statuses", Arrays.asList("packaging", "shipping", "delivery"))
                .getSingleResult()).intValue();

            // Calculate total revenue - using DISTINCT and grouping by order ID
            BigDecimal totalRevenue = (BigDecimal) em.createQuery(
                "SELECT COALESCE(SUM(DISTINCT o.totalPrice), 0) FROM Orders o WHERE o.status IN :statuses")
                .setParameter("statuses", Arrays.asList("packaging", "shipping", "delivery"))
                .getSingleResult();
                
            // Get recent reports
            List<Reports> recentReports = em.createQuery(
                "SELECT r FROM Reports r ORDER BY r.generatedDate DESC", Reports.class)
                .setMaxResults(5)
                .getResultList();
                
            // Set attributes
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
            request.setAttribute("error", "Error loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/admin/admin_dashboard.jsp").forward(request, response);
        }
    }
    
    private void generateReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            utx.begin();
            Users user = (Users) request.getSession().getAttribute("user");

            // Get and parse dates
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            // Query orders
            List<Orders> orders = em.createQuery(
                "SELECT DISTINCT o FROM Orders o " +
                "WHERE o.orderDate BETWEEN :startDate AND :endDate", Orders.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

            // Then fetch order details separately to avoid multiplication
            for (Orders order : orders) {
                em.refresh(order); // Ensure we have fresh data
                if (order.getOrderdetailsList() != null) {
                    order.getOrderdetailsList().size(); // Initialize the collection
                }
            }

            // Calculate metrics
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

            BigDecimal averageOrderValue = orders.isEmpty() ? BigDecimal.ZERO :
                totalRevenue.divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP);

            // Create and save report
            Reports report = new Reports();
            report.setReportType("Sales Report");
            report.setGeneratedDate(new Date());
            report.setGeneratedById(user);
            report.setDetails(formatReportDetails(
                startDate, endDate, orders.size(),
                totalRevenue, averageOrderValue,
                totalDiscounts, totalProducts,
                uniqueCustomers.size()
            ));

            em.persist(report);
            utx.commit();
            
            request.getSession().setAttribute("success", "Report generated successfully!");
            
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
            totalDiscounts, totalProducts, uniqueCustomers
        );
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
        request.setAttribute("error", "Error: " + e.getMessage());
        request.getRequestDispatcher("/admin/admin_dashboard.jsp").forward(request, response);
    }
}