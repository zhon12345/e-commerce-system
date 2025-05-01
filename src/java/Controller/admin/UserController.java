package Controller.admin;

import Model.Users;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import java.util.List;

/**
 *
 * @author IceCube
 */
public class UserController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Check if user is logged in and has appropriate role
			HttpSession session = request.getSession();
			Users currentUser = (Users) session.getAttribute("user");

			// If user is not logged in or is not staff/manager, throw a 403 error
			if (currentUser == null || !(currentUser.getRole().equalsIgnoreCase("staff") ||
					currentUser.getRole().equalsIgnoreCase("manager"))) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
				return;
			}

			List<Users> userList = em.createQuery(
					"SELECT u FROM Users u WHERE u.role = :role AND u.isArchived = :isArchived", Users.class)
					.setParameter("role", "customer")
					.setParameter("isArchived", false)
					.getResultList();

			request.setAttribute("userList", userList);
			request.getRequestDispatcher("/admin/admin_users.jsp").forward(request, response);
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
		Users currentUser = (Users) session.getAttribute("user");

		// If user is not logged in or is not staff/manager, throw a 403 error
		if (currentUser == null || !(currentUser.getRole().equalsIgnoreCase("staff") ||
				currentUser.getRole().equalsIgnoreCase("manager"))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
			return;
		}

		// For certain actions, check if user is a manager
		if (("add".equals(action) || "edit".equals(action) || "archive".equals(action))
				&& !currentUser.getRole().equalsIgnoreCase("manager")) {
			session.setAttribute("error", "Only managers can add, edit, or archive users");
			response.sendRedirect(request.getContextPath() + "/admin/users");
			return;
		}

		try {
			if ("add".equals(action)) {
				String username = request.getParameter("username");
				String email = request.getParameter("email");
				String password = request.getParameter("password");
				String role = request.getParameter("role");

				// Validate input
				if (username == null || username.trim().isEmpty()
						|| email == null || email.trim().isEmpty()
						|| password == null || password.trim().isEmpty()
						|| role == null || role.trim().isEmpty()) {

					session.setAttribute("error", "All fields are required");
					response.sendRedirect(request.getContextPath() + "/admin/users");
					return;
				}

				// Create new user
				Users newUser = new Users();
				newUser.setUsername(username.trim());
				newUser.setEmail(email.trim());
				newUser.setPassword(hashPassword(password));
				newUser.setRole(role.trim());
				newUser.setIsArchived(false);

				utx.begin();
				em.persist(newUser);
				utx.commit();

				session.setAttribute("success", "User added successfully!");
			} else if ("edit".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				String username = request.getParameter("username").trim();
				String email = request.getParameter("email").trim();
				String role = request.getParameter("role").trim();

				Users user = em.find(Users.class, id);
				if (user != null) {
					utx.begin();
					user.setUsername(username);
					user.setEmail(email);
					user.setRole(role);
					em.merge(user);
					utx.commit();

					session.setAttribute("success", "User updated successfully!");
				} else {
					session.setAttribute("error", "User not found!");
				}
			} else if ("archive".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				Users user = em.find(Users.class, id);
				if (user != null) {
					utx.begin();
					user.setIsArchived(true);
					em.merge(user);
					utx.commit();

					session.setAttribute("success", "User archived successfully!");
				} else {
					session.setAttribute("error", "User not found!");
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

		response.sendRedirect(request.getContextPath() + "/admin/users");
	}

	private String hashPassword(String password) throws ServletException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes());

			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException("Error hashing password", e);
		}
	}
}
