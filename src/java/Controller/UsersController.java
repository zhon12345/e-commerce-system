/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Users;
import static Utils.Authentication.hashPassword;
import static Utils.Authentication.isAuthorized;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author zhon12345
 */
@WebServlet(name = "UsersController", urlPatterns = { "/admin/profile", "/admin/users", "/admin/staff" })
public class UsersController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (!isAuthorized(user)) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if (user != null && user.getRole().equalsIgnoreCase("manager")) {
			session.setAttribute("isManager", true);
		}

		if (path.equals("/admin/profile")) {
			req.getRequestDispatcher("/admin/admin_profile.jsp").forward(req, res);
			return;
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
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (!isAuthorized(user)) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if (path.equals("/admin/profile")) {
			String action = req.getParameter("action");

			if (action.equals("change_password")) {
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
				}
			}

			if (action.equals("update_profile")) {
				updateUser(req, session, user.getId().toString(), "Profile");
			}

			res.sendRedirect(req.getContextPath() + path);
			return;
		}

		if (path.equals("/admin/users") || path.equals("/admin/staff")) {
			if (!user.getRole().equalsIgnoreCase("manager")) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			String userType = path.equals("/admin/users") ? "Users" : "Staff";
			String action = req.getParameter("action");

			switch (action) {
				case "create":
					createUser(req, session, userType);
					break;
				case "update":
				case "delete":
					String userId = req.getParameter("userId");

					if ("update".equals(action)) {
						updateUser(req, session, userId, userType);
					}

					if ("delete".equals(action)) {
						deleteUser(req, session, userId, userType);
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

	private void createUser(HttpServletRequest req, HttpSession session, String userType) throws IOException, ServletException {
		String username = req.getParameter("username").trim();
		String email = req.getParameter("email").trim();
		String password = req.getParameter("password").trim();
		String role = req.getParameter("role");

		if (username == null || username.trim().isEmpty()
				|| email == null || email.trim().isEmpty()
				|| password == null || password.trim().isEmpty()
				|| role == null || role.trim().isEmpty()) {

			session.setAttribute("error", "All fields are required");
			return;
		}

		try {
			String hashedPassword = hashPassword(password);

			utx.begin();
			Users newUser = new Users(username, email, hashedPassword, role);
			em.persist(newUser);
			utx.commit();

			session.setAttribute("success",
					role.substring(0, 1).toUpperCase() + role.substring(1) + " created successfully!");
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error",
					"Error creating " + role.substring(0, 1).toUpperCase() + role.substring(1) + ": " + e.getMessage());
		}
	}

	private void updateUser(HttpServletRequest req, HttpSession session, String userId, String userType) throws IOException, ServletException {
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
				session.setAttribute("error", "Username is already taken");
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

				if (user.getId().equals(((Users) session.getAttribute("user")).getId())) {
					session.setAttribute("user", user);
				}

				session.setAttribute("success", userType + " updated successfully!");
			} else {
				session.setAttribute("error", userType + " not found!");
			}

		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Error updating " + userType + ": " + e.getMessage());
		}
	}

	private void deleteUser(HttpServletRequest req, HttpSession session, String userId, String userType) throws IOException, ServletException {
		try {
			int id = Integer.parseInt(userId);
			Users user = em.find(Users.class, id);

			if (user != null) {
				utx.begin();
				user.setIsArchived(true);
				em.merge(user);
				utx.commit();

				session.setAttribute("success", userType + " deleted successfully!");
			} else {
				session.setAttribute("error", userType + " not found!");
			}
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("error", "Error deleting " + userType + ": " + e.getMessage());
		}
	}
}
