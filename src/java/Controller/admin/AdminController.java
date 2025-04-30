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

public class AdminController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/admin/admin_profile.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Users user = (Users) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		try {
			String action = request.getParameter("action");

			if ("change_password".equals(action)) {
				String currentPassword = request.getParameter("currentPassword");
				String newPassword = request.getParameter("newPassword");
				String confirmPassword = request.getParameter("confirmPassword");

				// Verify current password
				if (!user.getPassword().equals(hashPassword(currentPassword))) {
					session.setAttribute("error", "Current password is incorrect");
					response.sendRedirect(request.getContextPath() + "/admin/profile");
					return;
				}

				// Update password
				utx.begin();
				user.setPassword(hashPassword(newPassword));
				em.merge(user);
				utx.commit();

				session.setAttribute("success", "Password updated successfully");
			} else {
				// Update profile info
				String username = request.getParameter("username");
				String name = request.getParameter("name");
				String email = request.getParameter("email");

				// Validate inputs
				if (username == null || username.trim().isEmpty()
						|| name == null || name.trim().isEmpty()
						|| email == null || email.trim().isEmpty()) {
					session.setAttribute("error", "All fields are required");
					response.sendRedirect(request.getContextPath() + "/admin/profile");
					return;
				}

				// Validate username uniqueness if changed
				if (!username.equals(user.getUsername())) {
					Long usernameCount = em.createQuery(
							"SELECT COUNT(u) FROM Users u WHERE u.username = :username AND u.id != :userId", Long.class)
							.setParameter("username", username)
							.setParameter("userId", user.getId())
							.getSingleResult();

					if (usernameCount > 0) {
						session.setAttribute("error", "Username is already taken");
						response.sendRedirect(request.getContextPath() + "/admin/profile");
						return;
					}
				}

				// Your existing email validation code...
				// Update user
				utx.begin();
				user.setUsername(username.trim());
				user.setName(name);
				user.setEmail(email);
				em.merge(user);
				utx.commit();

				session.setAttribute("user", user);
				session.setAttribute("success", "Profile updated successfully");
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

		response.sendRedirect(request.getContextPath() + "/admin/profile");
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
