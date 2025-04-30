package Controller.admin;

import Model.Orders;
import Model.Products;
import Model.Users;
import java.io.IOException;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

public class AdminDashboard extends HttpServlet {
    
    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private UserTransaction utx;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Check if EntityManager and UserTransaction are available
            if (em == null || utx == null) {
                throw new ServletException("Database connection not available");
            }
            
            utx.begin();
            
            // Get total customers
            int totalUsers = em.createQuery(
                "SELECT COUNT(u) FROM Users u WHERE u.isArchived = ?1 AND u.role = ?2", Long.class)
                .setParameter(1, false)
                .setParameter(2, "customer")
                .getSingleResult()
                .intValue();
            
            // Get total products
            int totalProducts = em.createQuery(
                "SELECT COUNT(p) FROM Products p WHERE p.isArchived = ?1", Long.class)
                .setParameter(1, false)
                .getSingleResult()
                .intValue();
            
            // Get total orders
            int totalOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.status IN ?1", Long.class)
                .setParameter(1, java.util.Arrays.asList("packaging", "shipping", "delivery"))
                .getSingleResult()
                .intValue();
                
            // Set attributes
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalOrders", totalOrders);
            
            utx.commit();
            
            // Forward to dashboard
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
}