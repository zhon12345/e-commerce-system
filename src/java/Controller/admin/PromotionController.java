package Controller.admin;

import Model.Promotions;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;

public class PromotionController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			List<Promotions> promotionList = em.createQuery(
					"SELECT p FROM Promotions p WHERE p.isActive = :isActive", Promotions.class)
					.setParameter("isActive", true)
					.getResultList();

			request.setAttribute("promotionList", promotionList);
			request.getRequestDispatcher("/admin/admin_promotions.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/admin/dashboard");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			if ("add".equals(action)) {
				String promoCode = request.getParameter("promoCode");
				String discountStr = request.getParameter("discount");
				String validFromStr = request.getParameter("validFrom");
				String validToStr = request.getParameter("validTo");

				// Validate input
				if (promoCode == null || promoCode.trim().isEmpty()
						|| discountStr == null || discountStr.trim().isEmpty()
						|| validFromStr == null || validFromStr.trim().isEmpty()
						|| validToStr == null || validToStr.trim().isEmpty()) {

					session.setAttribute("error", "All fields are required");
					response.sendRedirect(request.getContextPath() + "/admin/promotions");
					return;
				}

				// Parse values
				int percentValue = Integer.parseInt(discountStr);
				BigDecimal discount = new BigDecimal(percentValue).divide(new BigDecimal("100"));
				Date validFrom = dateFormat.parse(validFromStr);
				Date validTo = dateFormat.parse(validToStr);

				// Get today's date without time component
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				today.set(Calendar.MILLISECOND, 0);

				// Validate start date
				if (validFrom.before(today.getTime())) {
					session.setAttribute("error", "Valid From date cannot be earlier than today");
					response.sendRedirect(request.getContextPath() + "/admin/promotions");
					return;
				}

				// Validate end date
				if (validFrom.after(validTo)) {
					session.setAttribute("error", "Valid From date must be before Valid To date");
					response.sendRedirect(request.getContextPath() + "/admin/promotions");
					return;
				}

				// Create new promotion
				Promotions newPromo = new Promotions();
				newPromo.setPromoCode(promoCode.trim().toUpperCase());
				newPromo.setDiscount(discount);
				newPromo.setValidFrom(validFrom);
				newPromo.setValidTo(validTo);
				newPromo.setIsActive(true);

				utx.begin();
				em.persist(newPromo);
				utx.commit();

				session.setAttribute("success", "Promotion added successfully!");
			} else if ("edit".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				String promoCode = request.getParameter("promoCode");
				int percentValue = Integer.parseInt(request.getParameter("discount"));
				BigDecimal discount = new BigDecimal(percentValue).divide(new BigDecimal("100"));
				Date validFrom = dateFormat.parse(request.getParameter("validFrom"));
				Date validTo = dateFormat.parse(request.getParameter("validTo"));

				// Get today's date without time component
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				today.set(Calendar.MILLISECOND, 0);

				// Validate start date
				if (validFrom.before(today.getTime())) {
					session.setAttribute("error", "Valid From date cannot be earlier than today");
					response.sendRedirect(request.getContextPath() + "/admin/promotions");
					return;
				}

				// Validate end date
				if (validFrom.after(validTo)) {
					session.setAttribute("error", "Valid From date must be before Valid To date");
					response.sendRedirect(request.getContextPath() + "/admin/promotions");
					return;
				}

				Promotions promo = em.find(Promotions.class, id);
				if (promo != null) {
					utx.begin();
					promo.setPromoCode(promoCode.trim().toUpperCase());
					promo.setDiscount(discount);
					promo.setValidFrom(validFrom);
					promo.setValidTo(validTo);
					em.merge(promo);
					utx.commit();

					session.setAttribute("success", "Promotion updated successfully!");
				}
			} else if ("delete".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));

				Promotions promo = em.find(Promotions.class, id);
				if (promo != null) {
					utx.begin();
					promo.setIsActive(false);
					em.merge(promo);
					utx.commit();

					session.setAttribute("success", "Promotion Archived successfully!");
				}
			}
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			session.setAttribute("error", "An error occurred: " + e.getMessage());
			e.printStackTrace();
		}

		response.sendRedirect(request.getContextPath() + "/admin/promotions");
	}
}
