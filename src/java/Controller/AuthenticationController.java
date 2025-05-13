package Controller;

import Model.Users;
import static Utils.Authentication.hashPassword;
import static Utils.Authentication.logoutUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "AuthenticationController", urlPatterns = { "/register", "/login", "/logout" })
public class AuthenticationController extends BaseController {

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

		if (path.equals("/register") || path.equals("/login")) {
			if (user != null) {
				redirectToPage(req, res, "/index");
				return;
			}
		}

		if (path.equals("/register")) {
			forwardToPage(req, res, "/register.jsp");
			return;
		}

		if (path.equals("/login")) {
			forwardToPage(req, res, "/login.jsp");
			return;
		}

		if (path.equals("/logout")) {
			handleLogout(req, res);
			return;
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

		if (path.equals("/register")) {
			handleRegister(req, res);
			return;
		}

		if (path.equals("/login")) {
			handleLogin(req, res);
			return;
		}

		if (path.equals("/logout")) {
			handleLogout(req, res);
			return;
		}

		sendError(req, res, HttpServletResponse.SC_NOT_FOUND);
	}

	private void handleRegister(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String username = req.getParameter("username") != null ? req.getParameter("username").trim() : "";
		String email = req.getParameter("email") != null ? req.getParameter("email").trim() : "";
		String password = req.getParameter("password") != null ? req.getParameter("password").trim() : "";
		String confirmPassword = req.getParameter("confirmPassword") != null ? req.getParameter("confirmPassword").trim() : "";

		Boolean hasErrors = false;

		if (username.isEmpty()) {
			req.setAttribute("usernameError", "Username is required.");
			hasErrors = true;
		}

		if (email.isEmpty()) {
			req.setAttribute("emailError", "Email is required.");
			hasErrors = true;
		} else if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
			req.setAttribute("emailError", "Invalid email format.");
			hasErrors = true;
		}

		if (password.isEmpty()) {
			req.setAttribute("passwordError", "Password is required.");
			hasErrors = true;
		} else if (password.length() < 8) {
			req.setAttribute("passwordError", "Password must be at least 8 characters long.");
			hasErrors = true;
		}

		if (confirmPassword.isEmpty()) {
			req.setAttribute("confirmPasswordError", "Confirm Password is required.");
			hasErrors = true;
		} else if (!confirmPassword.equals(password)) {
			req.setAttribute("confirmPasswordError", "Passwords do not match.");
			hasErrors = true;
		}

		if (!hasErrors) {
			hasErrors = checkExisting(req, res, "username", username, "usernameError");
			hasErrors |= checkExisting(req, res, "email", email, "emailError");
		}

		if (hasErrors) {
			req.setAttribute("username", username);
			req.setAttribute("email", email);

			forwardToPage(req, res, "/register.jsp");
			return;
		}

		handleTransaction(() -> {
			Users newUser = new Users(username, email, hashPassword(password), "customer");
			em.persist(newUser);
		}, req, "Registration Successful!", "Registration failed. Please try again.");

		if (req.getSession().getAttribute("error") != null) {
			forwardToPage(req, res, "/register.jsp");
			return;
		}

		redirectToPage(req, res, "/login");
	}

	private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String input = req.getParameter("username") != null ? req.getParameter("username").trim() : "";
		String password = req.getParameter("password") != null ? req.getParameter("password").trim() : "";
		String redirect = req.getParameter("redirect");

		req.setAttribute("username", input);

		Boolean hasErrors = false;

		if (input.isEmpty()) {
			req.setAttribute("usernameError", "Username or email is required.");
			hasErrors = true;
		}

		if (password.isEmpty()) {
			req.setAttribute("passwordError", "Password is required.");
			hasErrors = true;
		}

		if (hasErrors) {
			forwardToPage(req, res, "/login.jsp");
			return;
		}

		try {
			Users user = em.createQuery("SELECT u FROM Users u WHERE u.username = :input OR u.email = :input", Users.class)
					.setParameter("input", input)
					.getSingleResult();

			String hashedPassword = hashPassword(password);

			if (!user.getPassword().equals(hashedPassword)) {
				req.setAttribute("usernameError", "Invalid username / email or password.");
				req.setAttribute("passwordError", "Invalid username / email or password.");
				hasErrors = true;
			}

			if (!hasErrors) {
				HttpSession session = req.getSession();
				session.invalidate();
				session = req.getSession(true);
				session.setAttribute("user", user);

				if (!user.getRole().equals("customer")) {
					redirectToPage(req, res, "/admin/dashboard");
					return;
				}

				setSuccessMessage(req, "Login successful!");

				String targetPath = validateRedirect(redirect, session);
				redirectToPage(req, res, targetPath);
				return;
			}
		} catch (Exception e) {
			handleException(req, res, e);
			return;
		}

		forwardToPage(req, res, "/login.jsp");
	}

	private void handleLogout(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		logoutUser(req);

		req.getSession(true);
		setSuccessMessage(req, "Logout successful!");
		redirectToPage(req, res, "/index");
	}

	private boolean checkExisting(HttpServletRequest req, HttpServletResponse res, String field, String value, String attribute) throws ServletException, IOException {
		try {
			Long count = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u." + field + " = :value", Long.class)
					.setParameter("value", value)
					.getSingleResult();

			if (count > 0) {
				req.setAttribute(attribute, field.substring(0, 1).toUpperCase() + field.substring(1) + " already exists.");
				return true;
			}

			return false;
		} catch (Exception e) {
			handleException(req, res, e);
			return true;
		}
	}

	private String validateRedirect(String redirect, HttpSession session) {
		redirect = redirect != null ? redirect.trim() : "";
		String cleanPath = redirect.replaceAll("[^a-zA-Z0-9-?=]", "");

		if (cleanPath != null && !cleanPath.isEmpty()) {
			String[] segments = cleanPath.split("/");
			String lastSegment = segments.length > 0 ? segments[segments.length - 1] : "";

			List<String> userPages = Arrays.asList("profile", "address", "card", "history", "reviews");

			if (session.getAttribute("success") != null && ((String) session.getAttribute("success")).contains("Login")) {
				session.removeAttribute("success");
			}

			if (userPages.contains(lastSegment)) {
				return "/user/" + lastSegment;
			} else {
				return "/" + cleanPath;
			}
		}

		return "/index";
	}
}
