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
				forwardToPage(req, res, "/index.jsp");
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
				forwardToPage(req, res, "/admin/admin_promotions.jsp");
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
			case "delete":
				try {
					int promoId = Integer.parseInt(req.getParameter("promoId"));
					Promotions promotion = em.find(Promotions.class, promoId);

					if (promotion == null) {
						setErrorMessage(req, "Promotion not found");
						break;
					}

					if (action.equals("update")) {
						updatePromotion(req, promotion);
					}

					if (action.equals("delete")) {
						deletePromotion(req, promotion);
					}

				} catch (NumberFormatException e) {
					setErrorMessage(req, "Promotion not found");
				}
		}

		redirectToPage(req, res, "/admin/promotions");
	}

	private void createPromotion(HttpServletRequest req) throws IOException {
		handleTransaction(() -> {
			Promotions promo = new Promotions();

			if (processPromotionData(req, promo)) {
				em.persist(promo);
			}
		}, req, "Promotion added successfully!", "Failed to add promotion");
	}

	private void updatePromotion(HttpServletRequest req, Promotions promotion) throws IOException {
		handleTransaction(() -> {
			if (processPromotionData(req, promotion)) {
				em.merge(promotion);
			}
		}, req, "Promotion successfully updated!", "Failed to update promotion");
	}

	private void deletePromotion(HttpServletRequest req, Promotions promotion) throws IOException {
		handleTransaction(() -> {
			promotion.setIsActive(false);
			em.merge(promotion);
		}, req, "Promotion deleted successfully!", "Failed to delete promotion");
	}

	private boolean processPromotionData(HttpServletRequest req, Promotions promotion) {
		try {
			String promoCode = req.getParameter("promoCode").trim().toUpperCase();
			BigDecimal discount = new BigDecimal(req.getParameter("discount")).divide(new BigDecimal(100));

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date validFrom = dateFormat.parse(req.getParameter("validFrom"));
			Date validTo = dateFormat.parse(req.getParameter("validTo"));

			if (promoCode.isEmpty()) {
				return false;
			}

			if (validFrom.after(validTo)) {
				return false;
			}

			promotion.setPromoCode(promoCode);
			promotion.setDiscount(discount);
			promotion.setValidFrom(validFrom);
			promotion.setValidTo(validTo);

			return true;
		} catch (Exception e) {
			setErrorMessage(req, "Error processing promotion data: " + e.getMessage());
			return false;
		}
	}
}
