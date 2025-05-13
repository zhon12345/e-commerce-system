package Controller;

import Model.Cardinfo;
import Model.Users;
import static Utils.Authentication.isLoggedIn;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CardController", urlPatterns = { "/user/cards" })
public class CardController extends BaseController {

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

		if (!isLoggedIn(req, res, user, "cards")) return;

		String action = req.getParameter("action");

		if (action != null && !action.equals("update")) {
			sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if ("update".equals(action)) {
			Cardinfo card = validateOwnership(req, res, user);

			if (card == null) {
				return;
			}

			String expDate = String.format("%02d/%02d", card.getExpMonth(), card.getExpYear() % 100);

			req.setAttribute("updateCard", card);
			req.setAttribute("number", card.getCardNumber());
			req.setAttribute("name", card.getCardName());
			req.setAttribute("expiryDate", expDate);
		}

		loadCard(req, user);
		forwardToPage(req, res, "/user/card.jsp");
	}

	/**
	 *
	 * @param req servlet request
	 * @param res servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Users user = getCurrentUser(req);

		if (!isLoggedIn(req, res, user, "cards")) return;

		String action = req.getParameter("action");

		if (action == null || action.isEmpty()) {
			sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		switch (action) {
			case "create":
				createCard(req, res, user);
				break;
			case "update":
			case "delete":
				Cardinfo card = validateOwnership(req, res, user);

				if (card == null) {
					return;
				}

				if (action.equals("update")) {
					updateCard(req, res, card);
				}

				if (action.equals("delete")) {
					deleteCard(req, card);
				}
				break;
			default:
				sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
				return;
		}

		redirectToPage(req, res, "/user/cards");
	}

	private Cardinfo validateOwnership(HttpServletRequest req, HttpServletResponse res, Users user) throws IOException {
		try {
			int cardId = Integer.parseInt(req.getParameter("id"));
			Cardinfo card = em.find(Cardinfo.class, cardId);

			if (card == null || card.getUserId().getId() != user.getId()) {
				sendError(req, res, HttpServletResponse.SC_FORBIDDEN, true);
				return null;
			}

			return card;
		} catch (NumberFormatException e) {
			handleException(req, e, "Invalid card ID");
			return null;
		}
	}

	private void loadCard(HttpServletRequest req, Users user) {
		try {
			List<Cardinfo> cards = em.createQuery(
					"SELECT c FROM Cardinfo c WHERE c.userId = :user AND c.isArchived = :isArchived", Cardinfo.class)
					.setParameter("user", user)
					.setParameter("isArchived", false)
					.getResultList();

			req.setAttribute("cards", cards);
		} catch (Exception e) {
			handleException(req, e, "Error loading cards");
			req.setAttribute("cards", List.of());
		}
	}

	private void createCard(HttpServletRequest req, HttpServletResponse res, Users user) throws ServletException, IOException {
		Cardinfo card = new Cardinfo();
		boolean success = processCardData(req, card);

		if (!success) {
			doGet(req, res);
			return;
		}

		handleTransaction(() -> {
			card.setUserId(user);
			em.persist(card);
		}, req, "Card added successfully!", "Error adding card");
	}

	private void updateCard(HttpServletRequest req, HttpServletResponse res, Cardinfo card) throws ServletException, IOException {
		boolean success = processCardData(req, card);

		if (!success) {
			req.setAttribute("updateCard", card);
			doGet(req, res);
			return;
		}

		handleTransaction(() -> {
			em.merge(card);
		}, req, "Card updated successfully!", "Error updating card");
	}

	private void deleteCard(HttpServletRequest req, Cardinfo card) {
		handleTransaction(() -> {
			card.setIsArchived(true);
			em.merge(card);
		}, req, "Card deleted successfully!", "Error deleting card");
	}

	private boolean processCardData(HttpServletRequest req, Cardinfo card) {
		String number = req.getParameter("number") != null ? req.getParameter("number").trim() : "";
		String name = req.getParameter("name") != null ? req.getParameter("name").trim() : "";
		String expDate = req.getParameter("expiryDate") != null ? req.getParameter("expiryDate").trim() : "";

		Short expMonth = 0;
		Short expYear = 0;
		Boolean hasErrors = false;

		if (expDate.isEmpty()) {
			req.setAttribute("expiryDateError", "Expiry date is required");
			hasErrors = true;
		} else {
			try {
				String[] parts = expDate.split("/");
				if (parts.length != 2) {
					throw new IllegalArgumentException("Invalid format");
				}
				expMonth = Short.parseShort(parts[0]);
				expYear = Short.parseShort(parts[1]);

				if (expMonth < 1 || expMonth > 12) {
					req.setAttribute("expiryDateError", "Invalid month (01-12)");
					hasErrors = true;
				}
				int currentYear = java.time.Year.now().getValue() % 100;
				if (expYear < currentYear) {
					req.setAttribute("expiryDateError", "Card has expired");
					hasErrors = true;
				}
			} catch (Exception e) {
				req.setAttribute("expiryDateError", "Invalid format (MM/YY)");
				hasErrors = true;
			}
		}

		if (number.isEmpty()) {
			req.setAttribute("numberError", "Card number is required");
			hasErrors = true;
		} else if (!number.matches("\\d{12}")) {
			req.setAttribute("numberError", "Card number must be 12 digits");
			hasErrors = true;
		}

		if (name.isEmpty()) {
			req.setAttribute("nameError", "Cardholder name is required");
			hasErrors = true;
		}

		if (hasErrors) {
			req.setAttribute("number", number);
			req.setAttribute("name", name);
			req.setAttribute("expiryDate", expDate);

			return false;
		}

		card.setCardNumber(number);
		card.setCardName(name);
		card.setExpMonth(expMonth);
		card.setExpYear(expYear);
		return true;
	}
}
