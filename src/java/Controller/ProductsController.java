package Controller;

import Model.Categories;
import Model.Products;
import Model.Reviews;
import Model.Users;
import static Utils.Authentication.isAuthorized;
import Utils.FileManager;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductsController", urlPatterns = {"/products", "/product", "/admin/products"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class ProductsController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Resource
	private UserTransaction utx;

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
				HttpSession session = req.getSession();
				Users user = (Users) session.getAttribute("user");

				if (isAuthorized(user)) {
					fetchAdminProductsList(req, res);
					return;
				}
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

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String path = req.getServletPath();

		if (!path.equals("/admin/products")) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (!isAuthorized(user)) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String action = req.getParameter("action");

		switch (action) {
			case "create":
				createProduct(req, res);
				break;
			case "update":
			case "delete":
				String productId = req.getParameter("productId");

				Products product = em.find(Products.class, Integer.parseInt(productId));

				if (product == null) {
					session.setAttribute("error", "Product not found.");
					res.sendRedirect(req.getContextPath() + "/admin/products");
					return;
				}

				if (action.equals("update")) {
					updateProduct(req, res, product);
				}

				if (action.equals("delete")) {
					deleteProduct(req, res, product);
				}
		}

		res.sendRedirect(req.getContextPath() + "/admin/products");
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
		req.getRequestDispatcher("/product.jsp").forward(req, res);

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
		req.getRequestDispatcher("/products.jsp").forward(req, res);
	}

	private void fetchAdminProductsList(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		List<Products> productList = em
				.createQuery("SELECT p FROM Products p LEFT JOIN FETCH p.categoryId WHERE p.isArchived = :isArchived",
						Products.class)
				.setParameter("isArchived", false)
				.getResultList();

		// Get all categories
		List<Categories> categoryList = em.createNamedQuery("Categories.findAll", Categories.class)
				.getResultList();

		req.setAttribute("productList", productList);
		req.setAttribute("categoryList", categoryList);
		req.getRequestDispatcher("/admin/admin_products.jsp").forward(req, res);
	}

	private void createProduct(HttpServletRequest req, HttpServletResponse res) {
		try {
			Products product = new Products();
			product.setName(req.getParameter("name"));
			product.setDescription(req.getParameter("description"));

			// Set price (convert from String to BigDecimal)
			String priceStr = req.getParameter("price");
			BigDecimal price = new BigDecimal(priceStr);
			product.setPrice(price);

			// Set stock (convert from String to int)
			String stockStr = req.getParameter("stock");
			int stock = Integer.parseInt(stockStr);
			product.setStock(stock);

			// Set category if provided
			String categoryIdStr = req.getParameter("categoryId");
			if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
				int categoryId = Integer.parseInt(categoryIdStr);
				Categories category = em.find(Categories.class, categoryId);
				if (category != null) {
					product.setCategoryId(category);
				}
			}

			Part filePart = req.getPart("productImage");
			String imagePath = null;
			try {
				if (filePart != null && filePart.getSize() > 0) {
					imagePath = FileManager.uploadProductImage(filePart, getServletContext(), null);
					product.setImagePath(imagePath);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Persist the product using utx
			utx.begin();
			em.persist(product);
			utx.commit();

			req.getSession().setAttribute("success", "Product created successfully.");
		} catch (Exception e) {
			req.getSession().setAttribute("error", "Error creating product: " + e.getMessage());
		}
	}

	private void updateProduct(HttpServletRequest req, HttpServletResponse res, Products product) {
		try {
			String oldName = product.getName();
			String oldImagePath = product.getImagePath();

			// Update product fields
			product.setName(req.getParameter("name"));
			product.setDescription(req.getParameter("description"));

			// Update price
			String priceStr = req.getParameter("price");
			BigDecimal price = new BigDecimal(priceStr);
			product.setPrice(price);

			// Update stock
			String stockStr = req.getParameter("stock");
			int stock = Integer.parseInt(stockStr);
			product.setStock(stock);

			// Update category if provided
			String categoryIdStr = req.getParameter("categoryId");
			if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
				int categoryId = Integer.parseInt(categoryIdStr);
				Categories category = em.find(Categories.class, categoryId);
				if (category != null) {
					product.setCategoryId(category);
				} else {
					product.setCategoryId(null);
				}
			} else {
				product.setCategoryId(null);
			}

			Part filePart = req.getPart("productImage");
			if (filePart != null && filePart.getSize() > 0) {
				try {
					String imagePath = FileManager.uploadProductImage(filePart, getServletContext(), oldImagePath);
					if (imagePath != null) {
						product.setImagePath(imagePath);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Update the product using utx
			utx.begin();
			em.merge(product);
			utx.commit();

			req.getSession().setAttribute("success", "Product updated successfully.");
		} catch (Exception e) {
			req.getSession().setAttribute("error", "Error updating product: " + e.getMessage());
		}
	}

	private void deleteProduct(HttpServletRequest req, HttpServletResponse res, Products product) {
		try {
			product.setIsArchived(true);

			utx.begin();
			em.merge(product);
			utx.commit();
			req.getSession().setAttribute("success", "Product deleted successfully.");
		} catch (Exception e) {
			req.getSession().setAttribute("error", "Error deleting product: " + e.getMessage());
		}
	}

}