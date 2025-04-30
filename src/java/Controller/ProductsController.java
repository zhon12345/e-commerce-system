package Controller;

import Model.Products;
import Model.Reviews;
import Model.Categories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
			Integer selectedRating = null;

			if (ratingParam != null && !ratingParam.isEmpty()) {
				try {
					selectedRating = Integer.parseInt(ratingParam);
				} catch (NumberFormatException e) {
				}
			}

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Products> cq = cb.createQuery(Products.class);
			Root<Products> product = cq.from(Products.class);

			cq.select(product).distinct(true);

			List<Predicate> predicates = new ArrayList<>();

			 // Use a TypedQuery to filter by isArchived
			TypedQuery<Products> query = em.createNamedQuery("Products.findByIsArchived", Products.class);
			query.setParameter("isArchived", false);
			List<Products> productsList = query.getResultList();

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

			if (selectedRating != null) {
				Subquery<Double> avgSubquery = cq.subquery(Double.class);
				Root<Reviews> reviewRoot = avgSubquery.from(Reviews.class);
				avgSubquery.select(cb.avg(reviewRoot.get("rating")));
				avgSubquery.where(cb.equal(reviewRoot.get("productId").get("id"), product.get("id")));
				predicates.add(cb.ge(avgSubquery, selectedRating.doubleValue()));
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

			cq.orderBy(cb.asc(product.get("name")));

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

			req.setAttribute("products", productsList);
			req.setAttribute("categories", categoriesList);
			req.setAttribute("averageRatings", averageRatings);
			req.getRequestDispatcher("/products.jsp").forward(req, res);

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
		doGet(req, res);
	}
}