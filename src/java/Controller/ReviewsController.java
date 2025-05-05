/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Products;
import Model.Reviews;
import Model.Users;
import static Utils.Authentication.isLoggedIn;
import static Utils.Authentication.isLoggedInAndAuthorized;
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
import java.util.Date;
import java.util.List;

/**
 *
 * @author zhon12345
 */
@WebServlet(name = "ReviewsController", urlPatterns = { "/user/reviews", "/admin/reviews" })
public class ReviewsController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (path.equals("/user/reviews")) {
			if (!isLoggedIn(req, res, user, "reviews")) return;

			try {
				List<Reviews> reviewsList = em.createQuery(
						"SELECT r FROM Reviews r LEFT JOIN FETCH r.productId WHERE r.userId.id = :user AND r.isArchived = :isArchived ORDER BY r.reviewDate DESC",
						Reviews.class)
						.setParameter("user", user.getId())
						.setParameter("isArchived", false)
						.getResultList();

				req.setAttribute("reviews", reviewsList);
				req.getRequestDispatcher("/user/reviews.jsp").forward(req, res);
				return;
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		if (path.equals("/admin/reviews")) {
			if (!isLoggedInAndAuthorized(req, res, user, null)) return;

			try {
				List<Reviews> reviewsList = em.createQuery(
						"SELECT r FROM Reviews r LEFT JOIN FETCH r.userId LEFT JOIN FETCH r.productId WHERE r.isArchived = :isArchived",
						Reviews.class)
						.setParameter("isArchived", false)
						.getResultList();

				req.setAttribute("reviews", reviewsList);
				req.getRequestDispatcher("/admin/admin_reviews.jsp").forward(req, res);
				return;
			} catch (Exception e) {
				throw new ServletException(e);
			}
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
		String path = req.getServletPath();
		HttpSession session = req.getSession();

		if (!path.equals("/user/reviews")) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String action = req.getParameter("action");

		switch (action) {
			case "create":
				createReview(req, res, session);
				break;
			case "delete":
				deleteReview(req, res, session);
				break;
			default:
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
				break;
		}

	}

	private void createReview(HttpServletRequest req, HttpServletResponse res, HttpSession session)
			throws ServletException, IOException {
		Users user = (Users) session.getAttribute("user");
		String productId = req.getParameter("productId");

		if (!isLoggedIn(req, res, user, "product?id=" + productId + "&tab=reviews")) return;
		
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

				res.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&tab=reviews");
				return;
			}

			Reviews review = new Reviews();
			review.setUserId(user);
			review.setProductId(em.find(Products.class, Integer.parseInt(productId)));
			review.setRating(Integer.parseInt(rating));
			review.setReview(reviewText);
			review.setIsArchived(false);
			review.setReviewDate(new Date());

			utx.begin();
			em.persist(review);
			utx.commit();

			session.removeAttribute("selectedRating");
			session.removeAttribute("reviewText");

			res.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&tab=reviews");
			return;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void deleteReview(HttpServletRequest req, HttpServletResponse res, HttpSession session)
			throws ServletException, IOException {
		Users user = (Users) session.getAttribute("user");
		String reviewId = req.getParameter("reviewId");

		try {
			utx.begin();
			Reviews review = em.find(Reviews.class, Integer.parseInt(reviewId));

			if (review != null && review.getUserId().getId().equals(user.getId())) {
				review.setIsArchived(true);
				em.merge(review);
				utx.commit();
				session.setAttribute("deleteSuccess", "true");
			}
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			session.setAttribute("deleteError", "true");
		}

		res.sendRedirect(req.getContextPath() + "/user/reviews");
		return;
	}

}
