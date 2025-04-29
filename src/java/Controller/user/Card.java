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

public class Card extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Resource
	private UserTransaction utx;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=card");
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
						}
						res.sendRedirect(req.getContextPath() + "/user/card");
						return;

					case "edit":
						req.setAttribute("editCard", card);
						req.setAttribute("number", card.getCardNumber());
						req.setAttribute("name", card.getCardName());
						String expDate = String.format("%02d/%02d", card.getExpMonth(), card.getExpYear() % 100);
						req.setAttribute("expiryDate", expDate);
						break;
				}
			} catch (Exception e) {
				res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
		}

		loadCard(req, user);
		req.getRequestDispatcher("/user/card.jsp").forward(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=card");
			return;
		}

		String action = req.getParameter("action");
		String number = req.getParameter("number").trim();
		String name = req.getParameter("name").trim();
		String expDate = req.getParameter("expiryDate").trim();

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

			loadCard(req, user);

			req.getRequestDispatcher("/user/card.jsp").forward(req, res);
			return;
		}

		try {
			utx.begin();
			Cardinfo card;

			if ("edit".equals(action)) {
				int id = Integer.parseInt(req.getParameter("id"));
				card = em.find(Cardinfo.class, id);

				if (card == null || card.getUserId().getId() != user.getId()) {
					res.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			} else {
				card = new Cardinfo();
				card.setUserId(user);
			}

			card.setCardNumber(number);
			card.setCardName(name);
			card.setExpMonth(expMonth);
			card.setExpYear(expYear);

			if ("edit".equals(action)) {
				em.merge(card);
				session.setAttribute("editSuccess", "true");
			} else {
				em.persist(card);
				session.setAttribute("addSuccess", "true");
			}

			utx.commit();
			res.sendRedirect(req.getContextPath() + "/user/card");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
			}
			
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void loadCard(HttpServletRequest req, Users user) {
		List<Cardinfo> cards = em.createQuery(
				"SELECT c FROM Cardinfo c WHERE c.userId = :user AND c.isArchived = :isArchived", Cardinfo.class)
				.setParameter("user", user)
				.setParameter("isArchived", false)
				.getResultList();

		req.setAttribute("card", cards);
	}
}
