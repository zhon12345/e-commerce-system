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
			req.getRequestDispatcher("/admin/admin_users.jsp").forward(req, res);
			return;
		}

		if (path.equals("/admin/staff")) {
			fetchUsers(req, "staff");
			req.getRequestDispatcher("/admin/admin_staff.jsp").forward(req, res);
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
					String userId = req.getParameter("userId");

					if ("update".equals(action)) {
						updateUser(req, userId, userType);
					}

					if ("delete".equals(action)) {
						deleteUser(req, userId, userType);
					}
			}

			res.sendRedirect(req.getContextPath() + path);
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
		String username = req.getParameter("username").trim();
		String email = req.getParameter("email").trim();
		String password = req.getParameter("password").trim();
		String role = req.getParameter("role");

		if (username == null || username.trim().isEmpty()
				|| email == null || email.trim().isEmpty()
				|| password == null || password.trim().isEmpty()
				|| role == null || role.trim().isEmpty()) {

			setErrorMessage(req, "All fields are required");
			return;
		}

		try {
			String hashedPassword = hashPassword(password);

			utx.begin();
			Users newUser = new Users(username, email, hashedPassword, role);
			em.persist(newUser);
			utx.commit();

			setSuccessMessage(req, userType + " created successfully!");
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			setErrorMessage(req, "Error creating " + role.substring(0, 1).toUpperCase() + role.substring(1) + ": " + e.getMessage());
		}
	}

	private void updateUser(HttpServletRequest req, String userId, String userType) throws IOException, ServletException {
		String username = req.getParameter("username").trim();
		String email = req.getParameter("email").trim();
		String name = req.getParameter("name");
		String role = req.getParameter("role");

		try {
			int id = Integer.parseInt(userId);
			Users user = em.find(Users.class, id);

			Long usernameCount = em
					.createQuery("SELECT COUNT(u) FROM Users u WHERE u.username = :username AND u.id != :userId", Long.class)
					.setParameter("username", username)
					.setParameter("userId", user.getId())
					.getSingleResult();

			if (usernameCount > 0) {
				setErrorMessage(req, "Username is already taken");
				return;
			}

			if (user != null) {
				utx.begin();
				user.setUsername(username);
				user.setEmail(email);

				if (name != null && !name.trim().isEmpty()) {
					user.setName(name);
				}

				if (role != null && !role.isEmpty()) {
					user.setRole(role);
				}

				em.merge(user);
				utx.commit();

				if (user.getId().equals((getCurrentUser(req)).getId())) {
					req.getSession().setAttribute("user", user);
				}

				setSuccessMessage(req, userType + " updated successfully!");
			} else {
				setErrorMessage(req, userType + " not found!");
			}

		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			setErrorMessage(req, "Error updating " + userType + ": " + e.getMessage());
		}
	}

	private void deleteUser(HttpServletRequest req, String userId, String userType) throws IOException, ServletException {
		try {
			int id = Integer.parseInt(userId);
			Users user = em.find(Users.class, id);

			if (user != null) {
				utx.begin();
				user.setIsArchived(true);
				em.merge(user);
				utx.commit();

				setSuccessMessage(req, userType + " deleted successfully!");
			} else {
				setErrorMessage(req, userType + " not found!");
			}
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			setErrorMessage(req, "Error deleting " + userType + ": " + e.getMessage());
		}
	}
}
