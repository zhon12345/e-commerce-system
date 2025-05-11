package Controller;

import Model.Users;
import static Utils.Authentication.hashPassword;
import static Utils.Authentication.isLoggedInAndAuthorized;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UsersController", urlPatterns = { "/admin/users", "/admin/staff" })
public class UsersController extends BaseController {

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

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		if (user != null && user.getRole().equalsIgnoreCase("manager")) {
			req.getSession().setAttribute("isManager", true);
		}

		if (path.equals("/admin/users")) {
			fetchUsers(req, "customer");
			forwardToPage(req, res, "/admin/admin_users.jsp");
			return;
		}

		if (path.equals("/admin/staff")) {
			fetchUsers(req, "staff");
			forwardToPage(req, res, "/admin/admin_staff.jsp");
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

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		if (path.equals("/admin/users") || path.equals("/admin/staff")) {
			if (!user.getRole().equalsIgnoreCase("manager")) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			String userType = path.equals("/admin/users") ? "Users" : "Staff";
			String action = req.getParameter("action");

			switch (action) {
				case "create":
					createUser(req, userType);
					break;
				case "update":
				case "delete":
					try {
						int userId = Integer.parseInt(req.getParameter("userId"));
						Users selectedUser = em.find(Users.class, userId);

						if (selectedUser == null) {
							setErrorMessage(req, userType + " not found!");
							break;
						}

						if ("update".equals(action)) {
							updateUser(req, selectedUser, userType);
						}

						if ("delete".equals(action)) {
							deleteUser(req, selectedUser, userType);
						}
					} catch (NumberFormatException e) {
						setErrorMessage(req, userType + " not found!");
						break;
					}
			}

			redirectToPage(req, res, path);
			return;
		}

		res.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private void fetchUsers(HttpServletRequest req, String role) throws ServletException, IOException {
		try {
			List<Users> userList = em.createQuery(
					"SELECT u FROM Users u WHERE u.role = :role AND u.isArchived = :isArchived", Users.class)
					.setParameter("role", role)
					.setParameter("isArchived", false)
					.getResultList();

			req.setAttribute("users", userList);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void createUser(HttpServletRequest req, String userType) throws IOException, ServletException {
		handleTransaction(() -> {
			Users user = new Users();

			if (processUserData(req, user)) {
				em.persist(user);
				em.flush();
				em.refresh(user);
			}
		}, req, userType + " created successfully", "Error creating " + userType);
	}

	private void updateUser(HttpServletRequest req, Users user, String userType) throws IOException, ServletException {
		handleTransaction(() -> {
			if (processUserData(req, user)) {
				em.merge(user);
			}
		}, req, userType + " updated successfully!", "Error updating " + userType);
	}

	private void deleteUser(HttpServletRequest req, Users user, String userType) throws IOException, ServletException {
		handleTransaction(() -> {
			user.setIsArchived(true);
			em.merge(user);
		}, req, userType + " deleted successfully!", "Error deleting " + userType);
	}

	private boolean processUserData(HttpServletRequest req, Users user) {
		String username = req.getParameter("username");
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String role = req.getParameter("role");

		boolean hasError = false;

		if (username == null || username.trim().isEmpty()
				|| email == null || email.trim().isEmpty()
				|| role == null || role.trim().isEmpty()) {
			setErrorMessage(req, "All fields are required");
			hasError = true;
		} else if (user.getId() == null && (password == null || password.trim().isEmpty())) {
			setErrorMessage(req, "All fields are required");
			hasError = true;
		}

		if (hasError) {
			return false;
		}

		Long userId = user.getId() != null ? user.getId() : 0L;

		Long usernameCount = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u.username = :username AND u.id != :userId", Long.class)
				.setParameter("username", username)
				.setParameter("userId", userId)
				.getSingleResult();

		if (usernameCount > 0) {
			setErrorMessage(req, "Username is already taken");
			hasError = true;
		}

		Long emailCount = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u.email = :email AND u.id != :userId", Long.class)
				.setParameter("email", email)
				.setParameter("userId", userId)
				.getSingleResult();

		if (emailCount > 0) {
			setErrorMessage(req, "Email is already taken");
			hasError = true;
		}

		if (hasError) {
			return false;
		}

		user.setUsername(username.trim());
		user.setEmail(email.trim());
		user.setRole(role.trim());

		if (name != null && !name.trim().isEmpty()) {
			user.setName(name.trim());
		}

		if (password != null && !password.trim().isEmpty()) {
			user.setPassword(hashPassword(password.trim()));
		}

		return true;
	}
}
