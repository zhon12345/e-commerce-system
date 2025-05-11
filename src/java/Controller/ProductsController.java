package Controller;

import Model.Categories;
import Model.Products;
import Model.Reviews;
import Model.Users;
import static Utils.Authentication.isLoggedInAndAuthorized;
import Utils.FileManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductsController", urlPatterns = { "/products", "/product", "/admin/products" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class ProductsController extends BaseController {

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

		try {
			if (path.equals("/product")) {
				fetchSingleProduct(req, res);
				return;
			}

			if (path.equals("/products")) {
				fetchProductsList(req, res);
				return;
			}

			if (path.equals("/admin/products")) {
				Users user = getCurrentUser(req);

				if (!isLoggedInAndAuthorized(req, res, user, null)) return;

				fetchAdminProductsList(req, res);
			}

			res.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			throw new ServletException(e);
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

		if (!path.equals("/admin/products")) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Users user = getCurrentUser(req);

		if (!isLoggedInAndAuthorized(req, res, user, null)) return;

		String action = req.getParameter("action");

		switch (action) {
			case "create":
				createProduct(req);
				break;
			case "update":
			case "delete":
				String productId = req.getParameter("productId");

				Products product = em.find(Products.class, Integer.parseInt(productId));

				if (product == null) {
					setErrorMessage(req, "Product not found.");
					redirectToPage(req, res, "/admin/products");
					return;
				}

				if (action.equals("update")) {
					updateProduct(req, product);
				}

				if (action.equals("delete")) {
					deleteProduct(req, product);
				}
		}

		redirectToPage(req, res, "/admin/products");
	}

	private void fetchSingleProduct(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession();

		String productId = req.getParameter("id");

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

		if (averageRating == null)
			averageRating = 0.0;

		req.setAttribute("product", product);
		req.setAttribute("averageRating", averageRating);
		req.setAttribute("reviewList", reviews);
		forwardToPage(req, res, "/product.jsp");
	}

	private void fetchProductsList(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String[] categoryParams = req.getParameterValues("category");
		String minPriceParam = req.getParameter("min");
		String maxPriceParam = req.getParameter("max");
		String ratingParam = req.getParameter("rating");
		String search = req.getParameter("search");

		BigDecimal minPrice = parsePrice(minPriceParam);
		BigDecimal maxPrice = parsePrice(maxPriceParam);

		Integer selectedRating = null;
		if (ratingParam != null && !ratingParam.isEmpty()) {
			try {
				selectedRating = Integer.parseInt(ratingParam);
			} catch (NumberFormatException e) {
			}
		}

		List<Products> productsList = em.createNamedQuery("Products.findByIsArchived", Products.class)
				.setParameter("isArchived", false)
				.getResultList();

		if (categoryParams != null && categoryParams.length > 0) {
			final String[] categories = categoryParams;
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

		if (minPrice != null) {
			final BigDecimal min = minPrice;
			productsList = productsList.stream()
					.filter(p -> p.getPrice().compareTo(min) >= 0)
					.collect(java.util.stream.Collectors.toList());
		}

		if (maxPrice != null) {
			final BigDecimal max = maxPrice;
			productsList = productsList.stream()
					.filter(p -> p.getPrice().compareTo(max) <= 0)
					.collect(java.util.stream.Collectors.toList());
		}

		if (search != null && !search.trim().isEmpty()) {
			final String searchLower = search.toLowerCase();
			productsList = productsList.stream()
					.filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(searchLower)) ||
							(p.getDescription() != null && p.getDescription().toLowerCase().contains(searchLower)))
					.collect(java.util.stream.Collectors.toList());
		}

		List<Categories> categoriesList = em.createNamedQuery("Categories.findAll", Categories.class)
				.getResultList();

		String avgQuery = "SELECT r.productId.id, AVG(CAST(r.rating as float)) FROM Reviews r GROUP BY r.productId.id";
		List<Object[]> avgResults = em.createQuery(avgQuery, Object[].class).getResultList();

		Map<Integer, Double> averageRatings = new HashMap<>();
		for (Object[] result : avgResults) {
			Integer productId = (Integer) result[0];
			Double avg = (Double) result[1];
			averageRatings.put(productId, avg);
		}

		if (selectedRating != null) {
			final Integer finalSelectedRating = selectedRating;
			final Map<Integer, Double> finalRatings = averageRatings;
			productsList = productsList.stream()
					.filter(p -> {
						Double avgRating = finalRatings.get(p.getId());
						return avgRating != null && Math.round(avgRating) == finalSelectedRating;
					})
					.collect(java.util.stream.Collectors.toList());
		}

		req.setAttribute("products", productsList);
		req.setAttribute("categories", categoriesList);
		req.setAttribute("averageRatings", averageRatings);
		forwardToPage(req, res, "/products.jsp");
	}

	private void fetchAdminProductsList(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		List<Products> productList = em
				.createQuery("SELECT p FROM Products p LEFT JOIN FETCH p.categoryId WHERE p.isArchived = :isArchived",
						Products.class)
				.setParameter("isArchived", false)
				.getResultList();

		List<Categories> categoryList = em.createNamedQuery("Categories.findAll", Categories.class)
				.getResultList();

		req.setAttribute("productList", productList);
		req.setAttribute("categoryList", categoryList);
		forwardToPage(req, res, "/admin/admin_products.jsp");
	}

	private void createProduct(HttpServletRequest req) {
		handleTransaction(() -> {
			Products product = new Products();
			boolean success = processProductData(req, product, null);

			if (success) em.persist(product);
		}, req, "Product added successfully!", "Error adding product");
	}

	private void updateProduct(HttpServletRequest req, Products product) {
		handleTransaction(() -> {
			String oldImagePath = product.getImagePath();
			boolean success = processProductData(req, product, oldImagePath);

			if (success) em.merge(product);
		}, req, "Product updated successfully!", "Error updating product");
	}

	private void deleteProduct(HttpServletRequest req, Products product) {
		handleTransaction(() -> {
			product.setIsArchived(true);
			em.merge(product);
		}, req, "Product deleted successfully!", "Error deleting product");
	}

	private boolean processProductData(HttpServletRequest req, Products product, String oldImagePath) {
		try {
			product.setName(req.getParameter("name"));
			product.setDescription(req.getParameter("description"));

			String priceStr = req.getParameter("price");
			BigDecimal price = new BigDecimal(priceStr);
			product.setPrice(price);

			String stockStr = req.getParameter("stock");
			int stock = Integer.parseInt(stockStr);
			product.setStock(stock);

			String categoryIdStr = req.getParameter("categoryId");
			if (categoryIdStr == null || categoryIdStr.isEmpty()) {
				setErrorMessage(req, "Category is required.");
				return false;
			}

			try {
				int categoryId = Integer.parseInt(categoryIdStr);
				Categories category = em.find(Categories.class, categoryId);

				if (category == null) {
					setErrorMessage(req, "Selected category not found.");
					return false;
				}

				product.setCategoryId(category);
			} catch (NumberFormatException e) {
				setErrorMessage(req, "Invalid category selected.");
				return false;
			}

			Part filePart = req.getPart("productImage");
			if (filePart != null && filePart.getSize() > 0) {
				try {
					String imagePath = FileManager.uploadProductImage(filePart, getServletContext(), oldImagePath);

					if (imagePath != null) product.setImagePath(imagePath);
				} catch (Exception e) {
					setErrorMessage(req, "Error uploading image: " + e.getMessage());
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			setErrorMessage(req, "Error processing product: " + e.getMessage());
			return false;
		}
	}

}