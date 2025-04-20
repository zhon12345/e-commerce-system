/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Users;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author zhon12345
 */
public class Login extends HttpServlet {

	@PersistenceContext
	EntityManager em;

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
		String input = req.getParameter("username").trim();
		String password = req.getParameter("password").trim();

		req.setAttribute("username", input);

		Boolean hasErrors = false;

		if (input.isEmpty()) {
			req.setAttribute("usernameError", "Username is required.");
			hasErrors = true;
		}

		if (password.isEmpty()) {
			req.setAttribute("passwordError", "Password is required.");
			hasErrors = true;
		}

		if (hasErrors) {
			req.getRequestDispatcher("/login.jsp").forward(req, res);
			return;
		}

		try {
			Users user = em.createQuery("SELECT u FROM Users u WHERE u.username = :input OR u.email = :input", Users.class)
					.setParameter("input", input)
					.getSingleResult();

			String hashedPassword = hashPassword(password);
			if (!user.getPassword().equals(hashedPassword)) {
				req.setAttribute("usernameError", "Invalid username/email or password.");
				req.setAttribute("passwordError", "Invalid username/email or password.");
				hasErrors = true;
			}

			if (!hasErrors) {
				HttpSession session = req.getSession();
				session.setAttribute("user", user);
				res.sendRedirect(req.getContextPath() + "/index.jsp");
				return;
			}
		} catch (NoResultException e) {
			req.setAttribute("usernameError", "Invalid username/email or password.");
			req.setAttribute("passwordError", "Invalid username/email or password.");
			hasErrors = true;
		}

		if (hasErrors) {
			req.getRequestDispatcher("/login.jsp").forward(req, res);
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
