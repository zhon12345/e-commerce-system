/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Promotions;
import Model.Users;
import static Utils.Authentication.isAuthorized;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zhon12345
 */
@WebServlet(name = "PromotionsController", urlPatterns = { "/index", "/admin/promotions" })
public class PromotionsController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

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

		if (path.equals("/index")) {
			try {
				List<Promotions> promotionsList = em.createQuery(
						"SELECT p FROM Promotions p WHERE p.isActive = :isActive AND CURRENT_DATE BETWEEN p.validFrom AND p.validTo",
						Promotions.class)
						.setParameter("isActive", true)
						.getResultList();

				req.setAttribute("promotions", promotionsList);
				req.getRequestDispatcher("/index.jsp").forward(req, res);
				return;
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		if (path.equals("/admin/promotions")) {
			if (!isAuthorized(user)) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			try {
				List<Promotions> promotionsList = em.createNamedQuery("Promotions.findByIsActive", Promotions.class)
						.setParameter("isActive", true)
						.getResultList();

				req.setAttribute("promotions", promotionsList);
				req.getRequestDispatcher("/admin/admin_promotions.jsp").forward(req, res);
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

		if (!path.equals("/admin/promotions")) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (!isAuthorized(user)) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String action = req.getParameter("action");

		switch (action) {
			case "create":
				createPromotion(req, res, session);
				break;
			case "update":
				updatePromotion(req, res, session);
				break;
			case "delete":
				deletePromotion(req, res, session);
				break;
			default:
				session.setAttribute("error", "Invalid action performed: " + action);
				break;
		}

		res.sendRedirect(req.getContextPath() + "/admin/promotions");
	}

	private void createPromotion(HttpServletRequest req, HttpServletResponse res, HttpSession session)
			throws IOException {
		try {
			String promoCode = req.getParameter("promoCode").trim().toUpperCase();
			BigDecimal discount = new BigDecimal(req.getParameter("discount")).divide(new BigDecimal(100));

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date validFrom = dateFormat.parse(req.getParameter("validFrom"));
			Date validTo = dateFormat.parse(req.getParameter("validTo"));

			Promotions newPromo = new Promotions();
			newPromo.setPromoCode(promoCode);
			newPromo.setDiscount(discount);
			newPromo.setValidFrom(validFrom);
			newPromo.setValidTo(validTo);

			utx.begin();
			em.persist(newPromo);
			utx.commit();

			session.setAttribute("success", "Promotion added successfully!");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Failed to add promotion: " + e.getMessage());
		}
	}

	private void updatePromotion(HttpServletRequest req, HttpServletResponse res, HttpSession session)
			throws IOException {
		try {
			int promoId = Integer.parseInt(req.getParameter("promoId"));
			String promoCode = req.getParameter("promoCode").trim().toUpperCase();
			BigDecimal discount = new BigDecimal(req.getParameter("discount")).divide(new BigDecimal(100));

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date validFrom = dateFormat.parse(req.getParameter("validFrom"));
			Date validTo = dateFormat.parse(req.getParameter("validTo"));

			utx.begin();
			Promotions promo = em.find(Promotions.class, promoId);
			if (promo != null) {
				promo.setPromoCode(promoCode);
				promo.setDiscount(discount);
				promo.setValidFrom(validFrom);
				promo.setValidTo(validTo);
				em.merge(promo);
			}
			utx.commit();

			session.setAttribute("success", "Promotion updated successfully!");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Failed to update promotion: " + e.getMessage());
		}

	}

	private void deletePromotion(HttpServletRequest req, HttpServletResponse res, HttpSession session)
			throws IOException {
		try {
			int promoId = Integer.parseInt(req.getParameter("promoId"));

			utx.begin();
			Promotions promo = em.find(Promotions.class, promoId);
			if (promo != null) {
				promo.setIsActive(false);
				em.merge(promo);
			}
			utx.commit();

			session.setAttribute("success", "Promotion deleted successfully!");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Failed to delete promotion: " + e.getMessage());
		}
	}

}
