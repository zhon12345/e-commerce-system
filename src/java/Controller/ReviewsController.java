package Controller;

import Model.Products;
import Model.Reviews;
import Model.Users;
import static Utils.Authentication.isLoggedIn;
import static Utils.Authentication.isLoggedInAndAuthorized;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet(name = "ReviewsController", urlPatterns = { "/user/reviews", "/admin/reviews" })
public class ReviewsController extends BaseController {

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
				forwardToPage(req, res, "/user/reviews.jsp");
				return;
			} catch (Exception e) {
				handleException(req, res, e);
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
				forwardToPage(req, res, "/admin/admin_reviews.jsp");
				return;
			} catch (Exception e) {
				handleException(req, res, e);
			}
		}

		sendError(req, res, HttpServletResponse.SC_NOT_FOUND);
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

		if (!path.equals("/user/reviews")) {
			sendError(req, res, HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Users user = getCurrentUser(req);
		String action = req.getParameter("action");

		if (action == null || action.isEmpty()) {
			sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		switch (action) {
			case "create":
				try {
					int productId = Integer.parseInt(req.getParameter("productId"));
					Products product = em.find(Products.class, productId);

					if (product == null) {
						setErrorMessage(req, "Product not found.");
						break;
					}

					if (!isLoggedIn(req, res, user, "product?id=" + productId + "&tab=reviews")) return;

					createReview(req, res, product, user);
				} catch (NumberFormatException e) {
					redirectToPage(req, res, "/products");
				}
				break;
			case "delete":
				if (!isLoggedIn(req, res, user, "reviews")) return;

				try {
					int reviewId = Integer.parseInt(req.getParameter("reviewId"));
					Reviews review = em.find(Reviews.class, reviewId);

					if (review == null || !review.getUserId().getId().equals(user.getId())) {
						setErrorMessage(req, "Review not found.");
						break;
					}

					deleteReview(req, review);
				} catch (NumberFormatException e) {
					setErrorMessage(req, "Review not found.");
				}
				break;
			default:
				sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
				return;
		}

		redirectToPage(req, res, "/user/reviews");
	}

	private void createReview(HttpServletRequest req, HttpServletResponse res, Products product, Users user) throws ServletException, IOException {
		Reviews review = new Reviews();
		boolean success = processReviewData(req, review);

		if (!success) {
			redirectToPage(req, res, "/product?id=" + product.getId() + "&tab=reviews");
			return;
		}

		handleTransaction(() -> {
			review.setUserId(user);
			review.setProductId(product);
			review.setReviewDate(new Date());
			em.persist(review);
		}, req, null, "Error creating review.");

		HttpSession session = req.getSession();
		session.removeAttribute("selectedRating");
		session.removeAttribute("reviewText");
		session.removeAttribute("ratingError");
		session.removeAttribute("reviewError");
	}


	private void deleteReview(HttpServletRequest req, Reviews review) throws ServletException, IOException {
		handleTransaction(() -> {
			review.setIsArchived(true);
			em.merge(review);
		}, req, "Review deleted successfully!", "Error deleting review");
	}

	private boolean processReviewData(HttpServletRequest req, Reviews review) {
		HttpSession session = req.getSession();
		String rating = req.getParameter("rating");
		String reviewText = req.getParameter("reviewText") != null ? req.getParameter("reviewText").trim() : "";

		boolean hasError = false;

		session.removeAttribute("ratingError");
		session.removeAttribute("reviewError");

		if (rating == null || rating.isEmpty()) {
			session.setAttribute("ratingError", "Please select a rating");
			hasError = true;
		} else {
			try {
				int ratingValue = Integer.parseInt(rating);

				if (ratingValue < 1 || ratingValue > 5) {
					session.setAttribute("ratingError", "Rating must be between 1 and 5");
					hasError = true;
				}
			} catch (NumberFormatException e) {
				session.setAttribute("ratingError", "Invalid rating value");
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
			return false;
		}

		review.setRating(Integer.parseInt(rating));
		review.setReview(reviewText);
		return true;
	}

}
