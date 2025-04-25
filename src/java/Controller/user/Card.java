/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.user;

import java.io.IOException;
import java.util.List;

import Model.Cardinfo;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author zhon12345
 */
public class Card extends HttpServlet {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        if (action != null) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                Cardinfo card = em.find(Cardinfo.class, id);

                if (card == null || card.getUserId().getId() != user.getId()) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                switch (action) {
                    case "delete":
                        try {
                        utx.begin();

                        card.setIsArchived(true);
                        em.merge(card);
                        utx.commit();

                        session.setAttribute("deleteSuccess", "true");
                    } catch (Exception e) {
                        try {
                            utx.rollback();
                        } catch (Exception ex) {
                        }

                        loadCardinfo(req, user);
                    }

                    res.sendRedirect(req.getContextPath() + "/user/card");
                    return;
                    case "edit":
                        req.setAttribute("editCard", card);
                        req.setAttribute("number", card.getCardNumber());
                        req.setAttribute("name", card.getCardHolderName());
                        req.setAttribute("expiryDate", card.getExpirationDate());
                        req.setAttribute("cvv", card.getCvv());
                        break;
                }
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        loadCardinfo(req, user);

        req.getRequestDispatcher("/user/card.jsp").forward(req, res);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Get action parameter first
        String action = req.getParameter("action");

        // Correct parameter names to match form fields
        String number = req.getParameter("number") != null ? req.getParameter("number").trim() : "";
        String name = req.getParameter("name") != null ? req.getParameter("name").trim() : "";
        String expiryDateStr = req.getParameter("expiryDate") != null ? req.getParameter("expiryDate").trim() : "";
        String cvv = req.getParameter("cvv") != null ? req.getParameter("cvv").trim() : "";

        boolean hasErrors = false;

        // Validation (updated to use correct parameter names)
        if (number.isEmpty()) {
            req.setAttribute("numberError", "Card number is required");
            hasErrors = true;
        }
        if (name.isEmpty()) {
            req.setAttribute("nameError", "Card holder name is required");
            hasErrors = true;
        }
        if (expiryDateStr.isEmpty()) {
            req.setAttribute("expiryDateError", "Expiry date is required");
            hasErrors = true;
        }
        if (cvv.isEmpty()) {
            req.setAttribute("cvvError", "CVV is required");
            hasErrors = true;
        }

        // Date parsing
        Date expiryDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
            dateFormat.setLenient(false);  // Strict parsing
            expiryDate = dateFormat.parse(expiryDateStr);
        } catch (ParseException e) {
            req.setAttribute("expiryDateError", "Invalid date format. Use MM/YY");
            hasErrors = true;
        }

        if (hasErrors) {
            // Repopulate form fields with correct attribute names
            req.setAttribute("number", number);
            req.setAttribute("name", name);
            req.setAttribute("expiryDate", expiryDateStr);
            req.setAttribute("cvv", cvv);
            loadCardinfo(req, user);
            req.getRequestDispatcher("/user/card.jsp").forward(req, res);
            return;
        }

        try {
            utx.begin();
            Cardinfo card;

            if ("edit".equalsIgnoreCase(action)) {
                // Edit existing card
                int id = Integer.parseInt(req.getParameter("id"));
                card = em.find(Cardinfo.class, id);

                if (card == null || card.getUserId().getId() != user.getId()) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                // Create new card
                card = new Cardinfo();
                card.setUserId(user);
            }

            // Update card details
            card.setCardNumber(number);
            card.setCardHolderName(name);
            card.setExpirationDate(expiryDate);
            card.setCvv(cvv);

            // Save changes
            if ("edit".equalsIgnoreCase(action)) {
                em.merge(card);
            } else {
                em.persist(card);
            }

            utx.commit();

            // Set success message
            session.setAttribute(action != null ? "editSuccess" : "addSuccess", "true");
            res.sendRedirect(req.getContextPath() + "/user/card");

        } catch (Exception e) {
            try {
                if (utx != null) {
                    utx.rollback();
                }
            } catch (Exception ex) {
            }

            // Reload data and show error
            req.setAttribute("error", "Failed to save card: " + e.getMessage());
            loadCardinfo(req, user);
            req.getRequestDispatcher("/user/card.jsp").forward(req, res);
        }
    }

    private void loadCardinfo(HttpServletRequest req, Users user) {
        List<Cardinfo> cards = em.createQuery(
                "SELECT c FROM Cardinfo c WHERE c.userId = :user AND c.isArchived = :isArchived",
                Cardinfo.class)
                .setParameter("user", user)
                .setParameter("isArchived", false)
                .getResultList();

        req.setAttribute("card", cards);
    }
}
