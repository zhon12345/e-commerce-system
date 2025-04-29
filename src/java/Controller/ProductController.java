/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import java.io.IOException;

import Model.Products;
import Model.Reviews;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author zhon12345
 */
public class ProductController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

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
		HttpSession session = req.getSession();

		String productId = req.getParameter("id");
		String tab = req.getParameter("tab");

		String ratingError = (String) session.getAttribute("ratingError");
		String reviewError = (String) session.getAttribute("reviewError");
		String selectedRating = (String) session.getAttribute("selectedRating");
		String reviewText = (String) session.getAttribute("reviewText");

		if (ratingError != null) {
			req.setAttribute("ratingError", ratingError);
			session.removeAttribute("ratingError");
		}

		if (reviewError != null) {
			req.setAttribute("reviewError", reviewError);
			session.removeAttribute("reviewError");
		}

		if (selectedRating != null) {
			req.setAttribute("selectedRating", selectedRating);
		}

		if (reviewText != null) {
			req.setAttribute("reviewText", reviewText);
		}

		req.setAttribute("activeTab", tab != null ? tab : "description");

		try {
			Products product = em.createNamedQuery("Products.findById", Products.class)
					.setParameter("id", Integer.parseInt(productId))
					.getSingleResult();

			Double averageRating = em.createQuery(
					"SELECT AVG(CAST(r.rating AS float)) FROM Reviews r WHERE r.productId.id = :productId",
					Double.class).setParameter("productId", Integer.parseInt(productId))
					.getSingleResult();

			List<Reviews> reviews = em.createQuery(
					"SELECT r FROM Reviews r LEFT JOIN FETCH r.userId WHERE r.productId.id = :productId AND r.isArchived = :isArchived ORDER BY r.reviewDate DESC",
					Reviews.class)
					.setParameter("productId", Integer.parseInt(productId))
					.setParameter("isArchived", false)
					.getResultList();

			if (averageRating == null) averageRating = 0.0;

			req.setAttribute("product", product);
			req.setAttribute("averageRating", averageRating);
			req.setAttribute("reviewList", reviews);
			req.getRequestDispatcher("/product.jsp").forward(req, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	}
}