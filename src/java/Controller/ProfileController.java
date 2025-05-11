package Controller;

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

@WebServlet(name = "ProfileController", urlPatterns = { "/admin/profile", "/user/profile" })
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

}
