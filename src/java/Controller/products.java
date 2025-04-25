/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Products;
import Model.Categories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "products", urlPatterns = {"/products"})
public class products extends HttpServlet {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Products> productsList = em.createNamedQuery("Products.findAll", Products.class)
                    .getResultList();

            List<Categories> categoriesList = em.createNamedQuery("Categories.findAll", Categories.class)
                    .getResultList();

            request.setAttribute("categories", categoriesList);
            request.setAttribute("products", productsList);
            request.getRequestDispatcher("/products.jsp").forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}
