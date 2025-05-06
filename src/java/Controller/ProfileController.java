/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.user;

import Model.Addresses;
import Model.Cardinfo;
import Model.Users;
import static Utils.Authentication.hashPassword;
import static Utils.Authentication.isAuthorized;
import static Utils.Authentication.isLoggedIn;
import Utils.FileManager;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author zhon12345
 */
@WebServlet(name = "ProfileController", urlPatterns = { "/admin/profile", "/user/profile", "/user/address", "/user/card" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 10)
public class ProfileController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Resource
	private UserTransaction utx;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");
		
		String redirect = path.substring(path.lastIndexOf('/') + 1);

		if (!isLoggedIn(req, res, user, redirect)) return;

		if (path.equals("/admin/profile")) {
			if (!isAuthorized(user)) return;

			req.getRequestDispatcher("/admin/admin_profile.jsp").forward(req, res);
			return;
		}

		if (path.equals("/user/profile")) {
			req.getRequestDispatcher("/user/profile.jsp").forward(req, res);
			return;
		}

		String action = req.getParameter("action");
		String id = req.getParameter("id");

		if (path.equals("/user/address")) {
			if (action.equals("update")) {
				try {
					int addressId = Integer.parseInt(id);
					Addresses address = em.find(Addresses.class, addressId);

					if (address == null || address.getUserId().getId() != user.getId()) {
						res.sendError(HttpServletResponse.SC_FORBIDDEN);
						return;
					}

					req.setAttribute("updateAddress", address);
					req.setAttribute("name", address.getReceiverName());
					req.setAttribute("phone", address.getContactNumber());
					req.setAttribute("line1", address.getAddress1());
					req.setAttribute("line2", address.getAddress2());
					req.setAttribute("postcode", address.getPostalCode());
					req.setAttribute("city", address.getCity());
					req.setAttribute("state", address.getState());

				} catch (Exception e) {
					throw new ServletException(e);
				}
			}

			loadAddresses(req, user);
			req.getRequestDispatcher("/user/address.jsp").forward(req, res);
			return;
		}

		if (path.equals("/user/card")) {
			if (action.equals("update")) {
				try {
					int cardId = Integer.parseInt(id);
					Cardinfo card = em.find(Cardinfo.class, cardId);

					if (card == null || card.getUserId().getId() != user.getId()) {
						res.sendError(HttpServletResponse.SC_FORBIDDEN);
						return;
					}

					String expDate = String.format("%02d/%02d", card.getExpMonth(), card.getExpYear() % 100);

					req.setAttribute("updateCard", card);
					req.setAttribute("number", card.getCardNumber());
					req.setAttribute("name", card.getCardName());
					req.setAttribute("expiryDate", expDate);

				} catch (Exception e) {
					throw new ServletException(e);
				}
			}

			loadCard(req, user);
			req.getRequestDispatcher("/user/card.jsp").forward(req, res);
			return;
		}

		res.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param req
	 * @param res
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		String redirect = path.substring(path.lastIndexOf('/') + 1);

		if (!isLoggedIn(req, res, user, redirect)) return;

		String action = req.getParameter("action");

		if (path.equals("/admin/profile")) {
			if (!isAuthorized(user)) return;

			switch (action) {
				case "change_password":
					changePassword(req, session, user);
					break;
				case "update_profile":
					updateProfile(req, res, session, user);
					break;
				default:
					res.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
			}

			res.sendRedirect(req.getContextPath() + "/admin/profile");
			return;
		}

		if (path.equals("/user/profile")) {
			updateProfile(req, res, session, user);

			res.sendRedirect(req.getContextPath() + "/user/profile");
			return;
		}

		String id = req.getParameter("id");

		if (path.equals("/user/address")) {
			String name = req.getParameter("name") != null ? req.getParameter("name").trim() : "";
			String phone = req.getParameter("phone") != null ? req.getParameter("phone").trim() : "";
			String line1 = req.getParameter("line1") != null ? req.getParameter("line1").trim() : "";
			String line2 = req.getParameter("line2") != null ? req.getParameter("line2").trim() : "";
			String postcode = req.getParameter("postcode") != null ? req.getParameter("postcode").trim() : "";
			String city = req.getParameter("city") != null ? req.getParameter("city").trim() : "";
			String state = req.getParameter("state") != null ? req.getParameter("state").trim() : "";

			Boolean hasErrors = false;

			if (!action.equals("delete")) {
				if (name.isEmpty()) {
					req.setAttribute("nameError", "Receiver name is required");
					hasErrors = true;
				}
				if (phone.isEmpty()) {
					req.setAttribute("phoneError", "Phone is required");
					hasErrors = true;
				}
				if (line1.isEmpty()) {
					req.setAttribute("line1Error", "Address Line 1 is required");
					hasErrors = true;
				}
				if (postcode.isEmpty()) {
					req.setAttribute("postcodeError", "Postcode is required");
					hasErrors = true;
				}
				if (city.isEmpty()) {
					req.setAttribute("cityError", "City is required");
					hasErrors = true;
				}
				if (state.isEmpty()) {
					req.setAttribute("stateError", "State is required");
					hasErrors = true;
				}
			}

			if (hasErrors) {
				req.setAttribute("name", name);
				req.setAttribute("phone", phone);
				req.setAttribute("line1", line1);
				req.setAttribute("line2", line2);
				req.setAttribute("city", city);
				req.setAttribute("state", state);
				req.setAttribute("postcode", postcode);

				loadAddresses(req, user);

				req.getRequestDispatcher("/user/address.jsp").forward(req, res);
				return;
			}

			switch (action) {
				case "create":
					try {
						Addresses address = new Addresses(user, name, phone, line1, line2, city, state, postcode);

						utx.begin();
						em.persist(address);
						utx.commit();

						session.setAttribute("createSuccess", "Address added successfully!");
					} catch (Exception e) {
						try {
							if (utx != null) {
								utx.rollback();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						session.setAttribute("error", "Error adding address: " + e.getMessage());
					}
					break;
				case "update":
				case "delete":
					int addressId = Integer.parseInt(id);
					Addresses address = em.find(Addresses.class, addressId);

					if (address == null || address.getUserId().getId() != user.getId()) {
						res.sendError(HttpServletResponse.SC_FORBIDDEN);
						return;
					}

					if (action.equals("update")) {
						address.setReceiverName(name);
						address.setContactNumber(phone);
						address.setAddress1(line1);
						address.setAddress2(line2);
						address.setCity(city);
						address.setPostalCode(postcode);
						address.setState(state);

						try {
							utx.begin();
							em.merge(address);
							utx.commit();

							session.setAttribute("updateSuccess", "Address updated successfully!");
						} catch (Exception e) {
							try {
								if (utx != null) {
									utx.rollback();
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							session.setAttribute("error", "Error updating address: " + e.getMessage());
						}
					}

					if (action.equals("delete")) {
						deleteAddress(session, address);
					}
					break;
				default:
					res.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
			}

			res.sendRedirect(req.getContextPath() + "/user/address");
			return;
		}

		if (path.equals("/user/card")) {
			String number = req.getParameter("number") != null ? req.getParameter("number").trim() : "";
			String name = req.getParameter("name") != null ? req.getParameter("name").trim() : "";
			String expDate = req.getParameter("expiryDate") != null ? req.getParameter("expiryDate").trim() : "";

			Short expMonth = 0;
			Short expYear = 0;
			Boolean hasErrors = false;

			if (!action.equals("delete")) {
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
			}

			if (hasErrors) {
				req.setAttribute("number", number);
				req.setAttribute("name", name);
				req.setAttribute("expiryDate", expDate);

				loadCard(req, user);

				req.getRequestDispatcher("/user/card.jsp").forward(req, res);
				return;
			}

			switch (action) {
				case "create":
					try {
						Cardinfo card = new Cardinfo(user, number, name, expMonth, expYear);

						utx.begin();
						em.persist(card);
						utx.commit();

						session.setAttribute("createSuccess", "Card added successfully!");
					} catch (Exception e) {
						try {
							if (utx != null) {
								utx.rollback();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						session.setAttribute("error", "Error adding card: " + e.getMessage());
					}
					break;
				case "update":
				case "delete":
					int cardId = Integer.parseInt(id);
					Cardinfo card = em.find(Cardinfo.class, cardId);

					if (card == null || card.getUserId().getId() != user.getId()) {
						res.sendError(HttpServletResponse.SC_FORBIDDEN);
						return;
					}

					if (action.equals("update")) {
						card.setCardNumber(number);
						card.setCardName(name);
						card.setExpMonth(expMonth);
						card.setExpYear(expYear);

						try {
							utx.begin();
							em.merge(card);
							utx.commit();

							session.setAttribute("updateSuccess", "Card updated successfully!");
						} catch (Exception e) {
							try {
								if (utx != null) {
									utx.rollback();
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							session.setAttribute("error", "Error updating card: " + e.getMessage());
						}
					}

					if (action.equals("delete")) {
						deleteCard(session, card);
					}
					break;
				default:
					res.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
			}

			res.sendRedirect(req.getContextPath() + "/user/card");
		}
	}

	private void updateProfile(HttpServletRequest req, HttpServletResponse res, HttpSession session, Users user) throws ServletException, IOException {
		String path = req.getServletPath();
		String username = req.getParameter("username").trim();
		String name = req.getParameter("name").trim();
		String email = req.getParameter("email").trim();
		String contact = req.getParameter("phone").trim();
		Part filePart = req.getPart("avatar");

		Boolean hasErrors = false;

		if (username.isEmpty()) {
			req.setAttribute("usernameError", "Username is required.");
			hasErrors = true;
		} else if (!username.equals(user.getUsername())) {
			try {
				Long count = em.createQuery(
						"SELECT COUNT(u) FROM Users u WHERE u.username = :username", Long.class)
						.setParameter("username", username)
						.getSingleResult();

				if (count > 0) {
					req.setAttribute("usernameError", "Username already in use");
					hasErrors = true;
				}
			} catch (Exception e) {
				req.setAttribute("usernameError", "An unexpected error occurred. Please try again.");
				hasErrors = true;
			}
		}

		if (name.isEmpty()) {
			req.setAttribute("nameError", "Name is required.");
			hasErrors = true;
		}

		if (email.isEmpty()) {
			req.setAttribute("emailError", "Email is required.");
			hasErrors = true;
		} else if (!email.equals(user.getEmail())) {
			try {
				Long count = em.createQuery(
						"SELECT COUNT(u) FROM Users u WHERE u.email = :email", Long.class)
						.setParameter("email", email)
						.getSingleResult();

				if (count > 0) {
					req.setAttribute("emailError", "Email already in use");
					hasErrors = true;
				}
			} catch (Exception e) {
				req.setAttribute("emailError", "An unexpected error occurred. Please try again.");
				hasErrors = true;
			}
		}

		if (contact.isEmpty()) {
			req.setAttribute("contactError", "Contact is required.");
			hasErrors = true;
		}

		String avatar = user.getAvatarPath();
		try {
			avatar = FileManager.uploadAvatar(filePart, getServletContext(), user.getAvatarPath());
		} catch (Exception e) {
			req.setAttribute("avatarError", "Upload failed: " + e.getMessage());
			hasErrors = true;
		}

		if (hasErrors) {
			req.setAttribute("username", username);
			req.setAttribute("name", name);
			req.setAttribute("email", email);
			req.setAttribute("phone", contact);

			if (path.equals("/admin/profile")) {
				req.getRequestDispatcher("/admin/admin_profile.jsp").forward(req, res);
			} else {
				req.getRequestDispatcher("/user/profile.jsp").forward(req, res);
			}
			return;
		}

		try {
			utx.begin();

			user.setUsername(username);
			user.setName(name);
			user.setEmail(email);
			user.setContact(contact);
			user.setAvatarPath(avatar);

			em.merge(user);
			utx.commit();

			session.setAttribute("user", user);
			session.setAttribute("success", "Profile updated successfully!");
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
			}

			throw new ServletException(e);
		}
	}

	private void changePassword(HttpServletRequest req, HttpSession session, Users user) throws ServletException {
		String currentPassword = req.getParameter("currentPassword");
		String newPassword = req.getParameter("newPassword");
		String confirmPassword = req.getParameter("confirmPassword");

		if (!user.getPassword().equals(hashPassword(currentPassword))) {
			session.setAttribute("error", "Current password is incorrect");
			return;
		}

		if (!newPassword.equals(confirmPassword)) {
			session.setAttribute("error", "New password and confirm password do not match");
			return;
		}

		try {
			utx.begin();
			user.setPassword(hashPassword(newPassword));
			em.merge(user);
			utx.commit();

			session.setAttribute("success", "Password updated successfully");
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Error updating password: " + e.getMessage());
		}
	}

	private void deleteAddress(HttpSession session, Addresses address) {
		try {
			utx.begin();

			address.setIsArchived(true);
			em.merge(address);

			utx.commit();

			session.setAttribute("deleteSuccess", "true");
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Error deleting address: " + e.getMessage());
		}
	}

	private void deleteCard(HttpSession session, Cardinfo card) {
		try {
			utx.begin();

			card.setIsArchived(true);
			em.merge(card);

			utx.commit();

			session.setAttribute("deleteSuccess", "true");
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Error deleting card: " + e.getMessage());
		}
	}

	private void loadAddresses(HttpServletRequest req, Users user) {
		List<Addresses> addresses = em.createQuery(
				"SELECT c FROM Addresses c WHERE c.userId = :user AND c.isArchived = :isArchived",
				Addresses.class)
				.setParameter("user", user)
				.setParameter("isArchived", false)
				.getResultList();

		req.setAttribute("addresses", addresses);
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
