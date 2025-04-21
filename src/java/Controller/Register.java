/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Users;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

/**
 *
 * @author zhon12345
 */
public class Register extends HttpServlet {

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String username = req.getParameter("username").trim();
		String email = req.getParameter("email").trim();
		String password = req.getParameter("password").trim();
		String confirmPassword = req.getParameter("confirmPassword").trim();

		req.setAttribute("username", username);
		req.setAttribute("email", email);

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
			hasErrors = checkExisting("username", username, "usernameError", req);
			hasErrors |= checkExisting("email", email, "emailError", req);
		}

		if (hasErrors) {
			req.getRequestDispatcher("/register.jsp").forward(req, res);
			return;
		}

		try {
			String hashedPassword = hashPassword(password);

			utx.begin();
			Users newUser = new Users(username, email, hashedPassword, "customer");
			em.persist(newUser);
			utx.commit();

			res.sendRedirect(req.getContextPath() + "/login.jsp?success=true");
		} catch (Exception e) {
			e.printStackTrace();

			req.setAttribute("error", "Registration failed. Please try again.");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
		}
	}

	private boolean checkExisting(String field, String value, String attribute, HttpServletRequest req) {
		try {
			Long count = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u." + field + " = :value", Long.class).setParameter("value", value).getSingleResult();

			if (count > 0) {
				req.setAttribute(attribute, field.substring(0, 1).toUpperCase() + field.substring(1) + " already exists.");
				return true;
			}

			return false;
		} catch (Exception e) {
			req.setAttribute("error", "An unexpected error occurred. Please try again.");
			return true;
		}
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
