package Controller;

import Model.Products;
import Model.Categories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductsController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			String[] categoryParams = req.getParameterValues("category");
			String minPriceParam = req.getParameter("min");
			String maxPriceParam = req.getParameter("max");
			String search = req.getParameter("search");

			BigDecimal minPrice = parsePrice(minPriceParam);
			BigDecimal maxPrice = parsePrice(maxPriceParam);

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Products> cq = cb.createQuery(Products.class);
			Root<Products> product = cq.from(Products.class);

			cq.select(product).distinct(true);

			List<Predicate> predicates = new ArrayList<>();

			if (minPrice != null || maxPrice != null) {
				Path<BigDecimal> pricePath = product.get("price");
				List<Predicate> pricePredicates = new ArrayList<>();

				if (minPrice != null) {
					pricePredicates.add(cb.ge(pricePath, minPrice));
				}
				if (maxPrice != null) {
					pricePredicates.add(cb.le(pricePath, maxPrice));
				}

				predicates.add(cb.and(pricePredicates.toArray(new Predicate[0])));
			}

			if (search != null && !search.trim().isEmpty()) {
				String searchTerm = "%" + search.toLowerCase() + "%";

				predicates.add(
						cb.or(
								cb.like(cb.lower(product.get("name")), searchTerm),
								cb.like(cb.lower(product.get("description")), searchTerm)));
			}

			if (categoryParams != null && categoryParams.length > 0) {
				Subquery<Integer> categorySubquery = cq.subquery(Integer.class);
				Root<Categories> categoryRoot = categorySubquery.from(Categories.class);

				categorySubquery.select(categoryRoot.get("id"))
						.where(categoryRoot.get("name").in((Object[]) categoryParams));

				predicates.add(product.get("categoryId").get("id").in(categorySubquery));
			}

			if (!predicates.isEmpty()) {
				cq.where(cb.and(predicates.toArray(new Predicate[0])));
			}

			// Order by product name
			cq.orderBy(cb.asc(product.get("name")));

			// Get results
			List<Products> productsList = em.createQuery(cq).getResultList();

			// Get categories for filter display
			List<Categories> categoriesList = em.createNamedQuery("Categories.findAll", Categories.class)
					.getResultList();

			req.setAttribute("categories", categoriesList);
			req.setAttribute("products", productsList);
			req.getRequestDispatcher("/products.jsp").forward(req, res);

		} catch (Exception e) {
			e.printStackTrace();
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