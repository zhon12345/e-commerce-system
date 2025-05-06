package Controller;

import Model.Promotions;
import Model.Users;
import static Utils.Authentication.isLoggedInAndAuthorized;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "PromotionsController", urlPatterns = { "/index", "/admin/promotions" })
public class PromotionsController extends BaseController {

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
			if (!isLoggedInAndAuthorized(req, res, user, null)) return;

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
	 *
	 * @param req servlet request
	 * @param res servlet response
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

		Users user = getCurrentUser(req);

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		String action = req.getParameter("action");

		switch (action) {
			case "create":
				createPromotion(req);
				break;
			case "update":
				updatePromotion(req);
				break;
			case "delete":
				deletePromotion(req);
				break;
			default:
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
		}

		res.sendRedirect(req.getContextPath() + "/admin/promotions");
	}

	private void createPromotion(HttpServletRequest req) throws IOException {
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

			setSuccessMessage(req, "Promotion added successfully!");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			setErrorMessage(req, "Failed to add promotion: " + e.getMessage());
		}
	}

	private void updatePromotion(HttpServletRequest req) throws IOException {
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

			setSuccessMessage(req, "Promotion updated successfully!");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			setErrorMessage(req, "Failed to update promotion: " + e.getMessage());
		}
	}

	private void deletePromotion(HttpServletRequest req) throws IOException {
		try {
			int promoId = Integer.parseInt(req.getParameter("promoId"));

			utx.begin();
			Promotions promo = em.find(Promotions.class, promoId);
			if (promo != null) {
				promo.setIsActive(false);
				em.merge(promo);
			}
			utx.commit();

			setSuccessMessage(req, "Promotion deleted successfully!");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			setErrorMessage(req, "Failed to delete promotion: " + e.getMessage());
		}
	}
}
