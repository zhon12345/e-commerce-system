package Controller.admin;

import Model.Categories;
import Model.Products;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author kyanl
 */

@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 5, // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
    )

public class AdminProduct extends HttpServlet {
  private static final String UPLOAD_DIR = "uploads/products";

  @PersistenceContext 
	EntityManager em;

  @Resource 
	UserTransaction utx;

@Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
			List<Products> products = em.createQuery("SELECT p FROM Products p LEFT JOIN FETCH p.categoryId WHERE p.isArchived = :isArchived", Products.class)
      		.setParameter("isArchived", false)
					.getResultList();

      List<Categories> categories = em.createNamedQuery("Categories.findAll", Categories.class)
					.getResultList();

			request.setAttribute("products", products);
      request.setAttribute("categories", categories);
      request.getRequestDispatcher("/admin/admin_products.jsp").forward(request, response);
    } catch (Exception e) {
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String action = req.getParameter("action");
    
    // Add this new condition for delete action
    if ("delete".equals(action)) {
        try {
            utx.begin();
            
            int productId = Integer.parseInt(req.getParameter("productId"));
            Products product = em.find(Products.class, productId);
            
            if (product != null) {
                product.setIsArchived(true); // Set isArchived to true
                em.merge(product);
                utx.commit();
                
                res.getWriter().write("success");
            } else {
                res.getWriter().write("Product not found");
            }
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Log rollback error
            }
            res.getWriter().write("Error: " + e.getMessage());
        }
        return;
    }
    
    if ("edit".equals(action)) {
        try {
            utx.begin();
            
            int productId = Integer.parseInt(req.getParameter("productId"));
            Products product = em.find(Products.class, productId);
            
            if (product != null) {
                String oldName = product.getName();
                String newName = req.getParameter("productName").trim();
                
                // Update product details
                product.setName(newName);
                product.setDescription(req.getParameter("productDescription").trim());
                product.setPrice(new BigDecimal(req.getParameter("productPrice")));
                product.setStock(Integer.parseInt(req.getParameter("productStock")));
                
                // Update category
                int categoryId = Integer.parseInt(req.getParameter("productCategory"));
                Categories category = em.find(Categories.class, categoryId);
                product.setCategoryId(category);

                // Handle image updates
                List<Part> productPictures = req.getParts().stream()
                    .filter(part -> "productPicture".equals(part.getName()) && part.getSize() > 0)
                    .collect(Collectors.toList());

                if (!productPictures.isEmpty()) {
                    String webAppPath = getServletContext().getRealPath("/");
                    File webAppDir = new File(webAppPath);
                    File projectRootDir = webAppDir.getParentFile().getParentFile();
                    String imagePath = "/assets/products/" + newName + "/";
                    
                    // If name changed, create new directory
                    File uploadDir = new File(projectRootDir, "web" + imagePath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    
                    // Delete old images if name changed
                    if (!oldName.equals(newName)) {
                        File oldDir = new File(projectRootDir, "web/assets/products/" + oldName);
                        if (oldDir.exists()) {
                            for (File file : oldDir.listFiles()) {
                                file.delete();
                            }
                            oldDir.delete();
                        }
                    }

                    // Save new images
                    int sequence = 1;
                    for (Part prodPic : productPictures) {
                        String contentType = prodPic.getContentType();
                        if (!contentType.startsWith("image/")) {
                            res.getWriter().write("Error: Only images are allowed");
                            utx.rollback();
                            return;
                        }

                        String extension = contentType.split("/")[1];
                        String fileName = sequence + "." + extension;
                        File file = new File(uploadDir, fileName);
                        try (InputStream input = prodPic.getInputStream()) {
                            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    product.setImagePath(imagePath + "1.png");
                }
                
                em.merge(product);
                utx.commit();
                
                res.getWriter().write("success");
            } else {
                res.getWriter().write("Product not found");
            }
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Log rollback error
            }
            res.getWriter().write("Error: " + e.getMessage());
        }
        return;
    }
    
    String prodName = req.getParameter("addProductName").trim();
    int prodCategory = Integer.parseInt(req.getParameter("addProductCategory"));
    double prodPrice = Double.valueOf(req.getParameter("addProductPrice"));
    int prodStock = Integer.parseInt(req.getParameter("addProductStock"));
    String prodDesc = req.getParameter("addProductDescription").trim();

    Boolean hasErrors = false;
    
    // Get all parts for the product pictures
    List<Part> productPictures = req.getParts().stream()
        .filter(part -> "addProductPicture".equals(part.getName()))
        .collect(Collectors.toList());

    if (!productPictures.isEmpty()) {
        try {
            String webAppPath = getServletContext().getRealPath("/");
            File webAppDir = new File(webAppPath);
            File projectRootDir = webAppDir.getParentFile().getParentFile();
            File uploadDir = new File(projectRootDir, UPLOAD_DIR + prodName);

            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            int sequence = 1;
            for (Part prodPic : productPictures) {
                if (prodPic != null && prodPic.getSize() > 0) {
                    String contentType = prodPic.getContentType();
                    if (!contentType.startsWith("image/")) {
                        req.setAttribute("productPicError", "Only images are allowed");
                        hasErrors = true;
                        break;
                    }

                    String extension = contentType.split("/")[1];
                    String fileName = sequence + "." + extension;
                    sequence++;

                    File file = new File(uploadDir, fileName);
                    try (InputStream input = prodPic.getInputStream()) {
                        Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (Exception e) {
            req.setAttribute("productPicError", "Upload failed: " + e.getMessage());
            hasErrors = true;
        }
    }

    try {
        utx.begin();

        Categories category = em.find(Categories.class, prodCategory);
        Products newProd = new Products(
        prodName, prodDesc, BigDecimal.valueOf(prodPrice), prodStock);
        newProd.setCategoryId(category);
        
        String webAppPath = getServletContext().getRealPath("/");
        File webAppDir = new File(webAppPath);
        File projectRootDir = webAppDir.getParentFile().getParentFile();
        String imagePath = "/assets/products/" + prodName + "/";
        File uploadDir = new File(projectRootDir, "web" + imagePath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        newProd.setImagePath(imagePath + "1.png");

        em.persist(newProd);
        utx.commit();

        HttpSession session = req.getSession();
        session.setAttribute("addSuccess", "true");

        res.sendRedirect(req.getContextPath() + "/admin/products");
    } catch (Exception e) {
        try {
            if (utx != null) {
                utx.rollback();
            }
        } catch (Exception ex) {
        }

        req.setAttribute("error", "An error occurred, please try again.");
    }
  }
}


