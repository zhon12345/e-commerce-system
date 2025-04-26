package Controller;

import Model.Product;
import Model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/admin/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductController {

    @PersistenceContext(unitName = "your-persistence-unit")
    private EntityManager em;

    @GET
    public List<Product> getAllProducts() {
        Query query = em.createNamedQuery("Products.findAll");
        return query.getResultList();
    }

    @GET
    @Path("/{id}")
    public Product getProductById(@PathParam("id") Integer id) {
        return em.find(Product.class, id);
    }

    @POST
    @Transactional
    public Product createProduct(Product product) {
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        em.persist(product);
        return product;
    }

    @POST
    @Path("/{id}")
    @Transactional
    public Product updateProduct(@PathParam("id") Integer id, Product productUpdates) {
        Product existingProduct = em.find(Product.class, id);
        if (existingProduct != null) {
            if (productUpdates.getName() != null) {
                existingProduct.setName(productUpdates.getName());
            }
            if (productUpdates.getDescription() != null) {
                existingProduct.setDescription(productUpdates.getDescription());
            }
            if (productUpdates.getPrice() > 0) {
                existingProduct.setPrice(productUpdates.getPrice());
            }
            if (productUpdates.getStock() >= 0) {
                existingProduct.setStock(productUpdates.getStock());
            }
            if (productUpdates.getImageUrl() != null) {
                existingProduct.setImageUrl(productUpdates.getImageUrl());
            }
            if (productUpdates.getIsActive() != null) {
                existingProduct.setIsActive(productUpdates.getIsActive());
            }
            if (productUpdates.getCategory() != null) {
                existingProduct.setCategory(productUpdates.getCategory());
            }
            em.merge(existingProduct);
        }
        return existingProduct;
    }

    @POST
    @Path("/delete/{id}")
    @Transactional
    public String deleteProduct(@PathParam("id") Integer id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            // Soft delete by setting isActive to false
            product.setIsActive(false);
            em.merge(product);
            return "Product deactivated successfully";
        }
        return "Product not found";
    }

    @GET
    @Path("/categories")
    public List<Category> getAllCategories() {
        Query query = em.createNamedQuery("Category.findAll");
        return query.getResultList();
    }
}