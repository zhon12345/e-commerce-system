package Controller;

import Model.Addresses;
import Model.Cardinfo;
import Model.Cart;
import Model.Orderdetails;
import Model.Orders;
import Model.Products;
import Model.Promotions;
import Model.Users;
import static Utils.Authentication.isLoggedIn;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "CheckoutController", urlPatterns = { "/checkout" })
public class CheckoutController extends BaseController {

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

		if (!isLoggedIn(req, res, user, "checkout")) return;

		try {
			List<Cart> cartList = getCartList(user);
			if (cartList == null || cartList.isEmpty()) {
				redirectToPage(req, res, "/products");
				return;
			}

			processPromoCode(req);

			List<Addresses> addressList = em.createQuery(
					"SELECT c FROM Addresses c WHERE c.userId = :user AND c.isArchived = :isArchived",
					Addresses.class)
					.setParameter("user", user)
					.setParameter("isArchived", false)
					.getResultList();

			List<Cardinfo> cardList = em.createQuery(
					"SELECT c FROM Cardinfo c WHERE c.userId = :user AND c.isArchived = :isArchived", Cardinfo.class)
					.setParameter("user", user)
					.setParameter("isArchived", false)
					.getResultList();

			CartController.calculateOrderSummary(cartList, req);

			req.setAttribute("addressList", addressList);
			req.setAttribute("cardList", cardList);

			forwardToPage(req, res, "/checkout.jsp");
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
		HttpSession session = req.getSession();
		Users user = getCurrentUser(req);

		if (!isLoggedIn(req, res, user, "checkout")) return;

		String addressId = req.getParameter("addressId");
		String payment = req.getParameter("paymentMethod");
		String cardId = null;
		String cvv = null;

		Boolean hasErrors = false;

		if (addressId == null || addressId.isEmpty()) {
			req.setAttribute("addressSelectorError", "Please select a delivery address.");
			hasErrors = true;
		}

		if (payment == null || payment.isEmpty()) {
			payment = "cash";
		}

		if ("card".equals(payment)) {
			cardId = req.getParameter("cardId");
			cvv = req.getParameter("cvv");

			if (cardId == null || cardId.isEmpty()) {
				req.setAttribute("cardSelectorError", "Please select a card.");
				hasErrors = true;
			} else if (cvv == null) {
				req.setAttribute("cvvError", "CVV is required.");
				hasErrors = true;
			} else if (cvv.length() != 3) {
				req.setAttribute("cvvError", "CVV must be at least 3 digits long.");
				hasErrors = true;
			}
		}

		if (hasErrors) {
			req.setAttribute("selectedAddressId", addressId);
			req.setAttribute("selectedPaymentMethod", payment);
			req.setAttribute("selectedCardId", cardId);
			req.setAttribute("enteredCvv", cvv);

			doGet(req, res);
			return;
		}

		final String paymentMethod = payment;

		Addresses address = em.find(Addresses.class, Integer.parseInt(addressId));
		if (address == null || address.getUserId().getId() != user.getId()) {
			sendError(req, res, HttpServletResponse.SC_FORBIDDEN, true);
			return;
		}

		Cardinfo card = paymentMethod.equals("card") ? em.find(Cardinfo.class, Integer.parseInt(cardId)) : null;
		if (paymentMethod.equals("card") && (card == null || card.getUserId().getId() != user.getId())) {
			sendError(req, res, HttpServletResponse.SC_FORBIDDEN, true);
			return;
		}

		List<Cart> cartList = getCartList(user);
		if (cartList.isEmpty()) {
			redirectToPage(req, res, "/products");
			return;
		}

		double subtotal = cartList.stream()
				.mapToDouble(item -> item.getProductId().getPrice().doubleValue() * item.getQuantity())
				.sum();

		Promotions appliedPromo = (Promotions) session.getAttribute("appliedPromo");

		if (appliedPromo != null) {
			try {
				appliedPromo = em.find(Promotions.class, appliedPromo.getId());

				if (!appliedPromo.getIsActive() || new Date().after(appliedPromo.getValidTo())) {
					session.removeAttribute("appliedPromo");
					appliedPromo = null;
				}
			} catch (Exception e) {
				appliedPromo = null;
			}
		}

		final Promotions promoCode = appliedPromo;
		double discount = appliedPromo != null ? subtotal * appliedPromo.getDiscount().doubleValue() : 0;
		double tax = subtotal * 0.06;
		double shipping = subtotal > 1000 ? 0 : 25.00;
		double total = subtotal - discount + tax + shipping;

		handleTransaction(() -> {
			Orders order = new Orders();
			order.setUserId(user);
			order.setAddressId(address);
			order.setPaymentMethod(paymentMethod);
			order.setCardId(card);
			order.setStatus("packaging");
			order.setTotalPrice(BigDecimal.valueOf(total));
			order.setDeliveryCost(BigDecimal.valueOf(shipping));
			order.setPromoId(promoCode);
			order.setDiscount(BigDecimal.valueOf(discount));
			order.setOrderDate(new Date());
			order.setOrderdetailsList(new ArrayList<>());
			em.persist(order);

			for (Cart item : cartList) {
				Products product = em.find(Products.class, item.getProductId().getId());

				Orderdetails orderDetail = new Orderdetails();
				orderDetail.setOrderId(order);
				orderDetail.setProductId(product);
				orderDetail.setQuantity(item.getQuantity());
				orderDetail.setPrice(product.getPrice());
				em.persist(orderDetail);

				order.getOrderdetailsList().add(orderDetail);

				int newQuantity = product.getStock() - item.getQuantity();
				product.setStock(newQuantity);
				em.merge(product);

				em.remove(em.merge(item));
			}

			em.flush();
			em.merge(order);
			em.refresh(order);

		}, req, "Your order has been placed successfully!", "Failed to place order.");

		session.removeAttribute("appliedPromo");

		if (session.getAttribute("error") != null) {
			redirectToPage(req, res, "/cart");
			return;
		}

		redirectToPage(req, res, "/user/history");
	}

	private List<Cart> getCartList(Users user) {
		return em.createQuery("SELECT c FROM Cart c JOIN FETCH c.productId WHERE c.userId = :user", Cart.class)
				.setParameter("user", user)
				.getResultList();
	}

	private void processPromoCode(HttpServletRequest req) {
		String promoCode = req.getParameter("promoCode");
		String removePromo = req.getParameter("removePromo");
		Promotions appliedPromo = (Promotions) req.getSession().getAttribute("appliedPromo");

		if (removePromo != null && appliedPromo != null && removePromo.equals(appliedPromo.getPromoCode())) {
			req.getSession().removeAttribute("appliedPromo");
		}

		if (promoCode != null && !promoCode.isEmpty()) {
			try {
				Promotions promo = em.createQuery(
						"SELECT p FROM Promotions p WHERE p.promoCode = :code AND p.isActive = :isActive AND CURRENT_DATE BETWEEN p.validFrom AND p.validTo",
						Promotions.class)
						.setParameter("code", promoCode)
						.setParameter("isActive", true)
						.getSingleResult();

				req.getSession().setAttribute("appliedPromo", promo);
			} catch (Exception e) {
				req.setAttribute("promoCodeError", "Invalid or expired promo code.");
				req.getSession().removeAttribute("appliedPromo");
			}
		}
	}
}
