package Controller;

import Model.Addresses;
import Model.Cardinfo;
import Model.Users;
import static Utils.Authentication.hashPassword;
import static Utils.Authentication.isAuthorized;
import static Utils.Authentication.isLoggedIn;
import Utils.FileManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProfileController", urlPatterns = { "/admin/profile", "/user/profile", "/user/address", "/user/card" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 10)
public class ProfileController extends BaseController {

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

		String redirect = path.substring(path.lastIndexOf('/') + 1);

		if (!isLoggedIn(req, res, user, redirect)) return;

		if (path.equals("/admin/profile")) {
			if (!isAuthorized(user)) return;

			forwardToPage(req, res, "/admin/admin_profile.jsp");
			return;
		}

		if (path.equals("/user/profile")) {
			forwardToPage(req, res, "/user/profile.jsp");
			return;
		}

		String action = req.getParameter("action");
		String id = req.getParameter("id");

		if (path.equals("/user/address")) {
			if ("update".equals(action)) {
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
			forwardToPage(req, res, "/user/address.jsp");
			return;
		}

		if (path.equals("/user/card")) {
			if ("update".equals(action)) {
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
			forwardToPage(req, res, "/user/card.jsp");
			return;
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
		Users user = getCurrentUser(req);

		String redirect = path.substring(path.lastIndexOf('/') + 1);

		if (!isLoggedIn(req, res, user, redirect)) return;

		String action = req.getParameter("action");

		if (path.equals("/admin/profile")) {
			if (!isAuthorized(user)) return;

			switch (action) {
				case "change_password":
					changePassword(req, user);
					break;
				case "update_profile":
					updateProfile(req, res, user);
					break;
				default:
					res.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
			}

			redirectToPage(req, res, "/admin/profile");
			return;
		}

		if (path.equals("/user/profile")) {
			updateProfile(req, res, user);

			redirectToPage(req, res, "/user/profile");
			return;
		}

		String id = req.getParameter("id");

		if (path.equals("/user/address")) {

			switch (action) {
				case "create":
					createAddress(req, res, user);
					break;
				case "update":
				case "delete":
					try {
						int addressId = Integer.parseInt(id);
						Addresses address = em.find(Addresses.class, addressId);

						if (address == null || address.getUserId().getId() != user.getId()) {
							setErrorMessage(req, "Address not found");
							redirectToPage(req, res, "/user/address");
							return;
						}

						if (action.equals("update")) {
							updateAddress(req, res, user, address);
						}

						if (action.equals("delete")) {
							deleteAddress(req, address);
						}
					} catch (NumberFormatException e) {
						setErrorMessage(req, "Address not found");
					}
			}

			redirectToPage(req, res, "/user/address");
			return;
		}

		if (path.equals("/user/card")) {
			switch (action) {
				case "create":
					createCard(req, res, user);
					break;
				case "update":
				case "delete":
					try {
						int cardId = Integer.parseInt(id);
						Cardinfo card = em.find(Cardinfo.class, cardId);

						if (card == null || card.getUserId().getId() != user.getId()) {
							setErrorMessage(req, "Card not found");
							redirectToPage(req, res, "/user/card");
							return;
						}

						if (action.equals("update")) {
							updateCard(req, res, user, card);
						}

						if (action.equals("delete")) {
							deleteCard(req, card);
						}
					} catch (NumberFormatException e) {
						setErrorMessage(req, "Card not found");
					}
			}

			redirectToPage(req, res, "/user/card");
		}
	}

	private void updateProfile(HttpServletRequest req, HttpServletResponse res, Users user) throws ServletException, IOException {
		String path = req.getServletPath();
		boolean success = processProfileData(req, user);

		if (!success) {
			if (path.equals("/admin/profile")) {
				forwardToPage(req, res, "/admin/admin_profile.jsp");
			} else {
				forwardToPage(req, res, "/user/profile.jsp");
			}
			return;
		}

		handleTransaction(() -> {
			em.merge(user);
			req.getSession().setAttribute("user", user);
		}, req, "Profile updated successfully", "Error updating profile");
	}

	private void changePassword(HttpServletRequest req, Users user) throws ServletException {
		String currentPassword = req.getParameter("currentPassword");
		String newPassword = req.getParameter("newPassword");
		String confirmPassword = req.getParameter("confirmPassword");

		if (!user.getPassword().equals(hashPassword(currentPassword))) {
			setErrorMessage(req, "Current password is incorrect");
			return;
		}

		if (!newPassword.equals(confirmPassword)) {
			setErrorMessage(req, "New password and confirm password do not match");
			return;
		}

		handleTransaction(() -> {
			user.setPassword(hashPassword(newPassword));
			em.merge(user);
		}, req, "Password updated successfully!", "Error updating password");
	}

	private boolean processProfileData(HttpServletRequest req, Users user) throws IOException, ServletException {
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
			if (filePart != null && filePart.getSize() > 0) {
				avatar = FileManager.uploadAvatar(filePart, getServletContext(), user.getAvatarPath());
			}
		} catch (Exception e) {
			req.setAttribute("avatarError", "Upload failed: " + e.getMessage());
			hasErrors = true;
		}

		if (hasErrors) {
			req.setAttribute("username", username);
			req.setAttribute("name", name);
			req.setAttribute("email", email);
			req.setAttribute("phone", contact);
			return false;
		}

		user.setUsername(username);
		user.setName(name);
		user.setEmail(email);
		user.setContact(contact);
		user.setAvatarPath(avatar);
		return true;
	}

	private void createAddress(HttpServletRequest req, HttpServletResponse res, Users user) throws ServletException, IOException {
		Addresses address = new Addresses();
		boolean success = processAddressData(req, address);

		if (!success) {
			loadAddresses(req, user);
			forwardToPage(req, res, "/user/address.jsp");
			return;
		}

		handleTransaction(() -> {
			address.setUserId(user);
			em.persist(address);
		}, req, "Address added successfully!", "Error adding address");
	}

	private void updateAddress(HttpServletRequest req, HttpServletResponse res, Users user, Addresses address) throws ServletException, IOException {
		boolean success = processAddressData(req, address);

		if (!success) {
			loadAddresses(req, user);
			forwardToPage(req, res, "/user/address.jsp");
			return;
		}

		handleTransaction(() -> {
			em.merge(address);
		}, req, "Address updated successfully!", "Error updating address");
	}

	private void deleteAddress(HttpServletRequest req, Addresses address) {
		handleTransaction(() -> {
			address.setIsArchived(true);
			em.merge(address);
		}, req, "Address deleted successfully!", "Error deleting address");
	}

	private boolean processAddressData(HttpServletRequest req, Addresses address) {
		String name = req.getParameter("name").trim();
		String phone = req.getParameter("phone").trim();
		String line1 = req.getParameter("line1").trim();
		String line2 = req.getParameter("line2") != null ? req.getParameter("line2").trim() : "";
		String postcode = req.getParameter("postcode").trim();
		String city = req.getParameter("city").trim();
		String state = req.getParameter("state").trim();

		Boolean hasErrors = false;

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

		if (hasErrors) {
			req.setAttribute("name", name);
			req.setAttribute("phone", phone);
			req.setAttribute("line1", line1);
			req.setAttribute("line2", line2);
			req.setAttribute("city", city);
			req.setAttribute("state", state);
			req.setAttribute("postcode", postcode);
			return false;
		}

		address.setReceiverName(name);
		address.setContactNumber(phone);
		address.setAddress1(line1);
		address.setAddress2(line2);
		address.setCity(city);
		address.setPostalCode(postcode);
		address.setState(state);
		return true;
	}

	private void createCard(HttpServletRequest req, HttpServletResponse res, Users user) throws ServletException, IOException {
		Cardinfo card = new Cardinfo();
		boolean success = processCardData(req, card);

		if (!success) {
			loadCard(req, user);
			forwardToPage(req, res, "/user/card.jsp");
			return;
		}

		handleTransaction(() -> {
			card.setUserId(user);
			em.persist(card);
		}, req, "Card added successfully!", "Error adding card");
	}

	private void updateCard(HttpServletRequest req, HttpServletResponse res, Users user, Cardinfo card) throws ServletException, IOException {
		boolean success = processCardData(req, card);

		if (!success) {
			loadCard(req, user);
			forwardToPage(req, res, "/user/card.jsp");
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

			return false;
		}

		card.setCardNumber(number);
		card.setCardName(name);
		card.setExpMonth(expMonth);
		card.setExpYear(expYear);
		return true;
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
