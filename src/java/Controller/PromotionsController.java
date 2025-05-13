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
import java.math.RoundingMode;
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
				handleException(req, res, e);
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

		if (!path.equals("/admin/promotions")) {
			sendError(req, res, HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Users user = getCurrentUser(req);

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		String action = req.getParameter("action");

		if (action == null || action.isEmpty()) {
			sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

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
					handleException(req, e, "Invalid promotion ID.");
				}
				break;
			default:
				sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
				return;
		}

		redirectToPage(req, res, "/admin/promotions");
	}

	private void createPromotion(HttpServletRequest req) throws IOException {
		Promotions promotion = new Promotions();
		boolean success = processPromotionData(req, promotion);

		if (!success) {
			return;
		}

		handleTransaction(() -> {
			em.persist(promotion);
		}, req, "Promotion added successfully!", "Failed to add promotion");
	}

	private void updatePromotion(HttpServletRequest req, Promotions promotion) throws IOException {
		boolean success = processPromotionData(req, promotion);

		if (!success) {
			return;
		}

		handleTransaction(() -> {
			em.merge(promotion);
		}, req, "Promotion successfully updated!", "Failed to update promotion");
	}

	private void deletePromotion(HttpServletRequest req, Promotions promotion) throws IOException {
		handleTransaction(() -> {
			promotion.setIsActive(false);
			em.merge(promotion);
		}, req, "Promotion deleted successfully!", "Failed to delete promotion");
	}

	private boolean processPromotionData(HttpServletRequest req, Promotions promotion) {
		String promoCode = req.getParameter("promoCode") != null ? req.getParameter("promoCode").trim().toUpperCase() : "";
		String discountParam = req.getParameter("discount");
		String validFromParam = req.getParameter("validFrom");
		String validToParam = req.getParameter("validTo");

		if (promoCode.isEmpty()) {
			setErrorMessage(req, "Promotion code is required");
			return false;
		}

		if (discountParam == null || discountParam.isEmpty()) {
			setErrorMessage(req, "Discount is required");
			return false;
		}

		if (validFromParam == null || validFromParam.isEmpty()) {
			setErrorMessage(req, "Valid from date is required");
			return false;
		}

		if (validToParam == null || validToParam.isEmpty()) {
			setErrorMessage(req, "Valid to date is required");
			return false;
		}

		BigDecimal discount;
		try {
			BigDecimal discountValue = new BigDecimal(discountParam);

			if (discountValue.compareTo(BigDecimal.ZERO) <= 0 || discountValue.compareTo(new BigDecimal(100)) > 0) {
				setErrorMessage(req, "Discount must be between 1 and 100");
				return false;
			}

			discount = discountValue.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		} catch (NumberFormatException e) {
			setErrorMessage(req, "Invalid discount value");
			return false;
		}

		Date validFrom, validTo;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			validFrom = dateFormat.parse(validFromParam);
			validTo = dateFormat.parse(validToParam);
		} catch (Exception e) {
			setErrorMessage(req, "Invalid date format");
			return false;
		}

		if (validFrom.after(validTo)) {
			setErrorMessage(req, "Valid from date cannot be after valid to date");
			return false;
		}

		promotion.setPromoCode(promoCode);
		promotion.setDiscount(discount);
		promotion.setValidFrom(validFrom);
		promotion.setValidTo(validTo);

		return true;
	}
}
