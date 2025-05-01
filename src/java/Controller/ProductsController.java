package Controller;

import Model.Products;
import Model.Reviews;
import Model.Categories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			String[] categoryParams = req.getParameterValues("category");
			String minPriceParam = req.getParameter("min");
			String maxPriceParam = req.getParameter("max");
			String ratingParam = req.getParameter("rating");
			String search = req.getParameter("search");

			BigDecimal minPrice = parsePrice(minPriceParam);
			BigDecimal maxPrice = parsePrice(maxPriceParam);
			// Parse the rating parameter up front
			final Integer selectedRating;
			if (ratingParam != null && !ratingParam.isEmpty()) {
				try {
					selectedRating = Integer.parseInt(ratingParam);
				} catch (NumberFormatException e) {
					selectedRating = null;
				}
			} else {
				selectedRating = null;
			}

			// Use the findByIsArchived named query with false to only get non-archived
			// products
			List<Products> productsList = em.createNamedQuery("Products.findByIsArchived", Products.class)
					.setParameter("isArchived", false)
					.getResultList();

			// Apply category filter if needed
			if (categoryParams != null && categoryParams.length > 0) {
				final String[] categories = categoryParams; // Create a final copy for the lambda
				productsList = productsList.stream()
						.filter(p -> {
							Categories category = p.getCategoryId();
							if (category == null) {
								return false;
							}
							String categoryName = category.getName();
							for (String param : categories) {
								if (param.equals(categoryName)) {
									return true;
								}
							}
							return false;
						})
						.collect(java.util.stream.Collectors.toList());
			}

			// Apply price filters
			if (minPrice != null) {
				final BigDecimal min = minPrice; // Create a final copy
				productsList = productsList.stream()
						.filter(p -> p.getPrice().compareTo(min) >= 0)
						.collect(java.util.stream.Collectors.toList());
			}

			if (maxPrice != null) {
				final BigDecimal max = maxPrice; // Create a final copy
				productsList = productsList.stream()
						.filter(p -> p.getPrice().compareTo(max) <= 0)
						.collect(java.util.stream.Collectors.toList());
			}

			// Apply search filter
			if (search != null && !search.trim().isEmpty()) {
				final String searchLower = search.toLowerCase(); // Create a final copy
				productsList = productsList.stream()
						.filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(searchLower)) ||
								(p.getDescription() != null && p.getDescription().toLowerCase().contains(searchLower)))
						.collect(java.util.stream.Collectors.toList());
			}

			// Get categories for filter options
			List<Categories> categoriesList = em.createNamedQuery("Categories.findAll", Categories.class)
					.getResultList();

			// Get average ratings
			String avgQuery = "SELECT r.productId.id, AVG(CAST(r.rating as float)) FROM Reviews r GROUP BY r.productId.id";
			List<Object[]> avgResults = em.createQuery(avgQuery, Object[].class).getResultList();

			Map<Integer, Double> averageRatings = new HashMap<>();
			for (Object[] result : avgResults) {
				Integer productId = (Integer) result[0];
				Double avg = (Double) result[1];
				averageRatings.put(productId, avg);
			}

			// Apply rating filter if needed - using the final selectedRating
			if (selectedRating != null) {
				final Map<Integer, Double> finalRatings = averageRatings; // Create a final copy
				productsList = productsList.stream()
						.filter(p -> {
							Double avgRating = finalRatings.get(p.getId());
							return avgRating != null && Math.round(avgRating) == selectedRating;
						})
						.collect(java.util.stream.Collectors.toList());
			}

			req.setAttribute("products", productsList);
			req.setAttribute("categories", categoriesList);
			req.setAttribute("averageRatings", averageRatings);
			req.getRequestDispatcher("/products.jsp").forward(req, res);

		} catch (Exception e) {
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private BigDecimal parsePrice(String price) {
		if (price == null || price.isEmpty()) {
			return null;
		}
		try {
			return new BigDecimal(price);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
}