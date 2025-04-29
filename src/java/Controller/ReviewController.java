/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Products;
import Model.Reviews;
import Model.Users;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zhon12345
 */
public class ReviewController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=reviews");
			return;
		}

		try {
			List<Reviews> reviewList = em.createQuery("SELECT r FROM Reviews r LEFT JOIN FETCH r.productId WHERE r.userId.id = :user AND r.isArchived = :isArchived ORDER BY r.reviewDate DESC", Reviews.class)
					.setParameter("user", user.getId())
					.setParameter("isArchived", false)
					.getResultList();

			req.setAttribute("reviewList", reviewList);
			req.getRequestDispatcher("/user/reviews.jsp").forward(req, res);
		} catch (Exception e) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		String action = req.getParameter("action");

		if ("remove".equals(action)) {
			removeReview(req, res, user);
		}

		if ("add".equals(action)) {
			addReview(req, res, user);
		}
	}

	private void removeReview(HttpServletRequest req, HttpServletResponse res, Users user) throws ServletException, IOException {
		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=reviews");
			return;
		}

		String reviewId = req.getParameter("reviewId");
		HttpSession session = req.getSession();

		try {
			utx.begin();
			Reviews review = em.find(Reviews.class, Integer.parseInt(reviewId));

			if (review != null && review.getUserId().getId().equals(user.getId())) {
				review.setIsArchived(true);
				em.merge(review);
				utx.commit();
				session.setAttribute("deleteSuccess", "true");
			} else {
				session.setAttribute("deleteError", "true");
			}
		} catch (Exception e) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		res.sendRedirect(req.getContextPath() + "/user/reviews");
	}

	private void addReview(HttpServletRequest req, HttpServletResponse res, Users user) throws ServletException, IOException {
		String productId = req.getParameter("productId");
		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=product?id=" + productId);
			return;
		}

		HttpSession session = req.getSession();
		String activeTab = req.getParameter("activeTab");

		try {
			String rating = req.getParameter("rating");
			String reviewText = req.getParameter("reviewText").trim();

			boolean hasError = false;

			session.removeAttribute("selectedRatingError");
			session.removeAttribute("reviewError");

			if (rating == null || rating.isEmpty()) {
				session.setAttribute("ratingError", "Please select a rating");
				hasError = true;
			} else {
				int ratingValue = Integer.parseInt(rating);

				if (ratingValue < 1 || ratingValue > 5) {
					session.setAttribute("ratingError", "Rating must be between 1 and 5");
					hasError = true;
				}
			}

			if (reviewText.isEmpty()) {
				session.setAttribute("reviewError", "Review cannot be empty");
				hasError = true;
			}

			if (hasError) {
				session.setAttribute("selectedRating", rating);
				session.setAttribute("reviewText", reviewText);

				res.sendRedirect(req.getContextPath() + "/product?id=" + productId);
				return;
			}

			utx.begin();

			Reviews review = new Reviews();
			review.setUserId(user);
			review.setProductId(em.find(Products.class, Integer.parseInt(productId)));
			review.setRating(Integer.parseInt(rating));
			review.setReview(reviewText);
			review.setIsArchived(false);
			review.setReviewDate(new Date());
			em.persist(review);

			utx.commit();

			session.setAttribute("reviewSuccess", "true");

			session.removeAttribute("selectedRating");
			session.removeAttribute("reviewText");
		} catch (Exception e) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		res.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&tab=" + (activeTab != null ? activeTab : "reviews"));
	}

}
