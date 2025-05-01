/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Model.Categories;
import Model.Products;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

		// Set isArchived to false for new products
		product.setIsArchived(false);

		// Persist the product using utx
		utx.begin();
		em.persist(product);
		utx.commit();

		// Handle image upload
		Part filePart = req.getPart("productImage");
		if (filePart != null && filePart.getSize() > 0) {
			uploadProductImage(filePart, product.getName(), null);
		}
	}

	private void updateProduct(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// Get the product ID
		String productIdStr = req.getParameter("productId");
		int productId = Integer.parseInt(productIdStr);

		// Find the product
		Products product = em.find(Products.class, productId);
		if (product != null) {
			String oldProductName = product.getName();

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

			// Update the product using utx
			utx.begin();
			em.merge(product);
			utx.commit();

			// Handle image upload
			Part filePart = req.getPart("productImage");
			if (filePart != null && filePart.getSize() > 0) {
				uploadProductImage(filePart, product.getName(), oldProductName);
			} else if (!oldProductName.equals(product.getName())) {
				// If name changed but no new image, migrate existing image if it exists
				renameProductImageFolder(oldProductName, product.getName());
			}
		}
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

			// Note: We don't delete product images when archiving a product
		}
	}

	/**
	 * Upload product image to the appropriate folder
	 */
	private void uploadProductImage(Part filePart, String productName, String oldProductName) throws IOException {
		if (filePart == null || filePart.getSize() <= 0) {
			return;
		}

		// Check file type - only allow jpg and png
		String contentType = filePart.getContentType();
		if (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png")) {
			throw new IOException("Only JPG and PNG images are allowed");
		}

		// Get file extension from content type
		String extension = contentType.split("/")[1];
		if (extension.equals("jpeg")) {
			extension = "jpg";
		}

		// Set the file name to "1" with the appropriate extension
		String fileName = "1." + extension;

		// Create safe directory name from product name
		String safeName = productName.replaceAll("\\s+", "_");

		// Get web application path (build directory)
		String webAppPath = getServletContext().getRealPath("/");
		File webAppDir = new File(webAppPath);

		// Navigate to project root (2 levels up from the build directory)
		File projectDir = webAppDir.getParentFile().getParentFile();

		// Create products directory in project web/assets/products
		File projectProductsDir = new File(projectDir, "web/assets/products");
		if (!projectProductsDir.exists()) {
			projectProductsDir.mkdirs();
		}

		// Create products directory in build web/assets/products
		File buildProductsDir = new File(webAppPath, "assets/products");
		if (!buildProductsDir.exists()) {
			buildProductsDir.mkdirs();
		}

		// Create product-specific directories in both locations
		File projectProductDir = new File(projectProductsDir, safeName);
		if (!projectProductDir.exists()) {
			projectProductDir.mkdirs();
		}

		File buildProductDir = new File(buildProductsDir, safeName);
		if (!buildProductDir.exists()) {
			buildProductDir.mkdirs();
		}

		// Save the file to both locations
		try (InputStream input = filePart.getInputStream()) {
			// Save to project directory
			File projectFile = new File(projectProductDir, fileName);
			Files.copy(input, projectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		// Save to build directory (need a new input stream)
		try (InputStream input = filePart.getInputStream()) {
			File buildFile = new File(buildProductDir, fileName);
			Files.copy(input, buildFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		// If product name changed, clean up old directories in both locations
		if (oldProductName != null && !oldProductName.equals(productName)) {
			String oldSafeName = oldProductName.replaceAll("\\s+", "_");

			// Clean up project directory
			File oldProjectProductDir = new File(projectProductsDir, oldSafeName);
			if (oldProjectProductDir.exists()) {
				File[] files = oldProjectProductDir.listFiles();
				if (files != null) {
					for (File oldFile : files) {
						oldFile.delete();
					}
				}
				oldProjectProductDir.delete();
			}

			// Clean up build directory
			File oldBuildProductDir = new File(buildProductsDir, oldSafeName);
			if (oldBuildProductDir.exists()) {
				File[] files = oldBuildProductDir.listFiles();
				if (files != null) {
					for (File oldFile : files) {
						oldFile.delete();
					}
				}
				oldBuildProductDir.delete();
			}
		}
	}

	/**
	 * Rename product image folder when product name changes, but no new image is
	 * uploaded
	 */
	private void renameProductImageFolder(String oldProductName, String newProductName) {
		try {
			// Get web application path (build directory)
			String webAppPath = getServletContext().getRealPath("/");
			File webAppDir = new File(webAppPath);

			// Navigate to project root (2 levels up from the build directory)
			File projectDir = webAppDir.getParentFile().getParentFile();

			// Get products directory in project web/assets/products
			File projectProductsDir = new File(projectDir, "web/assets/products");

			// Get products directory in build web/assets/products
			File buildProductsDir = new File(webAppPath, "assets/products");

			String oldSafeName = oldProductName.replaceAll("\\s+", "_");
			String newSafeName = newProductName.replaceAll("\\s+", "_");

			// Rename in project directory
			File oldProjectProductDir = new File(projectProductsDir, oldSafeName);
			File newProjectProductDir = new File(projectProductsDir, newSafeName);

			// Only try to rename if the old directory exists
			if (oldProjectProductDir.exists()) {
				// Create new directory if it doesn't exist
				if (!newProjectProductDir.exists()) {
					newProjectProductDir.mkdirs();
				}

				// Copy all files from old directory to new
				File[] files = oldProjectProductDir.listFiles();
				if (files != null) {
					for (File file : files) {
						File destFile = new File(newProjectProductDir, file.getName());
						Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}

					// Delete old directory and its contents
					for (File file : files) {
						file.delete();
					}
					oldProjectProductDir.delete();
				}
			}

			// Rename in build directory
			File oldBuildProductDir = new File(buildProductsDir, oldSafeName);
			File newBuildProductDir = new File(buildProductsDir, newSafeName);

			// Only try to rename if the old directory exists
			if (oldBuildProductDir.exists()) {
				// Create new directory if it doesn't exist
				if (!newBuildProductDir.exists()) {
					newBuildProductDir.mkdirs();
				}

				// Copy all files from old directory to new
				File[] buildFiles = oldBuildProductDir.listFiles();
				if (buildFiles != null) {
					for (File file : buildFiles) {
						File destFile = new File(newBuildProductDir, file.getName());
						Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}

					// Delete old directory and its contents
					for (File file : buildFiles) {
						file.delete();
					}
					oldBuildProductDir.delete();
				}
			}
		} catch (IOException e) {
			// Log error but continue - this is a migration operation
			e.printStackTrace();
		}
	}
}
