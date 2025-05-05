/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Model.Categories;
import Model.Products;
import Model.Users;
import Utils.FileManager;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author zhon12345
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1 MB
		maxFileSize = 5 * 1024 * 1024, // 5 MB
		maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class AdminProducts extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	// Base directory for product images - updated to use web/assets/products/
	private static final String PRODUCT_IMAGE_DIR = "web/assets/products";

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
		try {
			// Check if user is logged in and has appropriate role
			HttpSession session = req.getSession();
			Users user = (Users) session.getAttribute("user");

			// If user is not logged in or is not staff/manager, throw a 403 error
			if (user == null || !(user.getRole().equalsIgnoreCase("staff") || user.getRole().equalsIgnoreCase("manager"))) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
				return;
			}

			// Get all non-archived products with eager loading of their categories
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
		} catch (Exception e) {
			e.printStackTrace(); // Log the exception for debugging
			throw new ServletException(e);
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
		// Check if user is logged in and has appropriate role
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		// If user is not logged in or is not staff/manager, throw a 403 error
		if (user == null || !(user.getRole().equalsIgnoreCase("staff") || user.getRole().equalsIgnoreCase("manager"))) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to admin area");
			return;
		}

		String action = req.getParameter("action");

		try {
			if ("add".equals(action)) {
				addProduct(req, res);
				req.getSession().setAttribute("success", "Product added successfully.");
			} else if ("edit".equals(action)) {
				updateProduct(req, res);
				req.getSession().setAttribute("success", "Product updated successfully.");
			} else if ("delete".equals(action)) {
				deleteProduct(req, res);
				req.getSession().setAttribute("success", "Product archived successfully.");
			}

			// Redirect back to product list after any action
			res.sendRedirect(req.getContextPath() + "/admin/products");
		} catch (Exception e) {
			e.printStackTrace();
			req.getSession().setAttribute("error", "An error occurred: " + e.getMessage());
			res.sendRedirect(req.getContextPath() + "/admin/products");
		}
	}

	private void addProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// Create a new product
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
	}

	private void updateProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// Get the product ID
		String productIdStr = req.getParameter("productId");
		int productId = Integer.parseInt(productIdStr);

		// Find the product
		Products product = em.find(Products.class, productId);
		if (product == null) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
			return;
		}

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
	}

	private void deleteProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// Get the product ID
		String productIdStr = req.getParameter("productId");
		int productId = Integer.parseInt(productIdStr);

		// Find the product
		Products product = em.find(Products.class, productId);
		if (product != null) {
			// Instead of physically deleting, mark as archived
			product.setIsArchived(true);

			// Update the product using utx
			utx.begin();
			em.merge(product);
			utx.commit();

		}
	}

}
