package Controller;

import Model.Cart;
import Model.Products;
import Model.Promotions;
import Model.Users;
import static Utils.Authentication.isLoggedIn;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@WebServlet(name = "CartController", urlPatterns = { "/cart" })
public class CartController extends BaseController {

	/**
	 *
	 * @param req servlet request
	 * @param res servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Users user = getCurrentUser(req);

		if (!isLoggedIn(req, res, user, "cart")) return;

		try {
			List<Cart> cartList = em.createQuery("SELECT c FROM Cart c WHERE c.userId = :user", Cart.class)
					.setParameter("user", user)
					.getResultList();

			req.setAttribute("cartList", cartList);

			calculateOrderSummary(cartList, req);

			forwardToPage(req, res, "/cart.jsp");
		} catch (Exception e) {
			handleException(req, res, e);
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
		Users user = getCurrentUser(req);

		if (!isLoggedIn(req, res, user, "cart")) return;

		String action = req.getParameter("action");

		try {
			Integer productId = Integer.parseInt(req.getParameter("productId"));
			Integer quantity = req.getParameter("quantity") != null ? Integer.parseInt(req.getParameter("quantity")) : 1;

			Products product = em.find(Products.class, productId);
			if (product == null) {
				redirectToPage(req, res, "/products");
				return;
			}

			switch (action) {
				case "decrease":
				case "increase":
					updateCart(req, user, product, action);
					break;
				case "remove":
					removeFromCart(req, user, product);
					break;
				case "add":
					addToCart(req, user, product, quantity);
					break;
				default:
					sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
					return;
			}

			redirectToPage(req, res, "/cart");
		} catch (NumberFormatException | NullPointerException e) {
			handleException(req, e, "Invalid product ID or action");
			redirectToPage(req, res, "/products");
		} catch (Exception e) {
			handleException(req, res, e);
		}
	}

	private List<Cart> fetchCartItems(Users user, Products product) {
		return em
				.createQuery("SELECT c FROM Cart c WHERE c.userId = :user AND c.productId = :product", Cart.class)
				.setParameter("user", user)
				.setParameter("product", product)
				.getResultList();
	}

	private void addToCart(HttpServletRequest req, Users user, Products product, int quantity) throws Exception {
		handleTransaction(() -> {
			List<Cart> existingCartItem = fetchCartItems(user, product);

			if (!existingCartItem.isEmpty()) {
				Cart cartItem = existingCartItem.get(0);
				cartItem.setQuantity(cartItem.getQuantity() + quantity);
				em.merge(cartItem);
			} else {
				Cart cartItem = new Cart();
				cartItem.setUserId(user);
				cartItem.setProductId(product);
				cartItem.setQuantity(quantity);
				em.persist(cartItem);
			}
		}, req, null, "Failed to add product to cart");
	}

	private void updateCart(HttpServletRequest req, Users user, Products product, String action) throws Exception {
		handleTransaction(() -> {
			List<Cart> cartItems = fetchCartItems(user, product);

			if (!cartItems.isEmpty()) {
				Cart cartItem = cartItems.get(0);
				int currentQuantity = cartItem.getQuantity();

				if ("increase".equals(action)) {
					cartItem.setQuantity(currentQuantity + 1);
					em.merge(cartItem);
				} else if ("decrease".equals(action)) {
					if (currentQuantity > 1) {
						cartItem.setQuantity(currentQuantity - 1);
						em.merge(cartItem);
					} else {
						em.remove(cartItem);
					}
				}
			}
		}, req, null, "Failed to update cart");
	}

	private void removeFromCart(HttpServletRequest req, Users user, Products product) throws Exception {
		handleTransaction(() -> {
			List<Cart> cartItems = fetchCartItems(user, product);

			if (!cartItems.isEmpty()) {
				Cart cartItem = cartItems.get(0);
				em.remove(cartItem);
			}
		}, req, null, "Failed to remove product from cart");
	}

	public static void calculateOrderSummary(List<Cart> cartList, HttpServletRequest req) {
		double subtotal = 0;
		int totalItems = 0;
		double discount = 0;

		Promotions appliedPromo = (Promotions) req.getSession().getAttribute("appliedPromo");

		if (cartList != null && !cartList.isEmpty()) {
			for (Cart item : cartList) {
				subtotal += item.getProductId().getPrice().doubleValue() * item.getQuantity();
				totalItems += item.getQuantity();
			}
		}

		if (appliedPromo != null) {
			discount = subtotal * appliedPromo.getDiscount().doubleValue();
		}

		double tax = subtotal * 0.06;
		double shipping = subtotal == 0 || subtotal > 1000 ? 0 : 25.00;
		double total = subtotal - discount + shipping + tax;

		DecimalFormat df = new DecimalFormat("0.00");

		req.setAttribute("discount", df.format(discount));
		req.setAttribute("subtotal", df.format(subtotal));
		req.setAttribute("tax", df.format(tax));
		req.setAttribute("shipping", shipping == 0 ? "FREE" : "RM " + df.format(shipping));
		req.setAttribute("total", df.format(total));
		req.setAttribute("totalItems", totalItems);
	}
}
