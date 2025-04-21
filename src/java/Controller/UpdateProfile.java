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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1 MB
		maxFileSize = 1024 * 1024 * 10, // 10 MB
		maxRequestSize = 1024 * 1024 * 10 // 20 MB
)

/**
 *
 * @author zhon12345
 */
public class UpdateProfile extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Resource
	private UserTransaction utx;

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
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (user == null) {
			res.sendRedirect("/login.jsp");
			return;
		}

		String name = req.getParameter("name").trim();
		String email = req.getParameter("email").trim();
		String contact = req.getParameter("phone").trim();
		// Part filePart = req.getPart("avatar");

		Boolean hasErrors = false;

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

		// String avatarPath = user.getAvatar();
		// if (filePart != null && filePart.getSize() > 0) {
		// try {
		// String contentType = filePart.getContentType();
		// if (!contentType.startsWith("image/")) {
		// req.setAttribute("avatarError", "Invalid file type. Only images are
		// allowed.");
		// hasErrors = true;
		// } else {
		// String fileName = UUID.randomUUID().toString() + "_" +
		// Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

		// Path uploadDir =
		// Paths.get(getServletContext().getRealPath("/web/assets/avatars"));

		// if (!Files.exists(uploadDir)) {
		// Files.createDirectories(uploadDir);
		// }

		// Path filePath = uploadDir.resolve(fileName);
		// try (InputStream inputStream = filePart.getInputStream()) {
		// Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		// }

		// avatarPath = "/assets/avatars/" + fileName;
		// }
		// } catch (Exception e) {
		// req.setAttribute("avatarError", "An error occurred while uploading the
		// file.");
		// hasErrors = true;
		// }
		// }

		if (hasErrors) {
			req.setAttribute("name", name);
			req.setAttribute("email", email);
			req.setAttribute("phone", contact);
			req.getRequestDispatcher("/user/profile.jsp").forward(req, res);
			return;
		}

		try {
			utx.begin();

			user.setName(name);
			user.setEmail(email);
			user.setContact(contact);
			// user.setAvatar(avatarPath);

			em.merge(user);
			utx.commit();

			session.setAttribute("user", em.merge(user));
			session.setAttribute("editSuccess", "true");

			res.sendRedirect(req.getContextPath() + "/user/profile.jsp");
		} catch (Exception e) {
			try {
				if (utx != null) {
					utx.rollback();
				}
			} catch (Exception ex) {
			}

			req.setAttribute("error", "An error occurred, please try again.");
			req.getRequestDispatcher("/user/profile.jsp").forward(req, res);
		}

	}

}
