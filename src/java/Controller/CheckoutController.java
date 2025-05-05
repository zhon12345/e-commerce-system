/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zhon12345
 */
public class CheckoutController extends HttpServlet {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction utx;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (!isLoggedIn(req, res, user, "checkout")) return;

		String promoCode = req.getParameter("promoCode");
		String removePromo = req.getParameter("removePromo");
		Promotions appliedPromo = (Promotions) session.getAttribute("appliedPromo");

		try {
			utx.begin();

			Users fullUser = em.find(Users.class, user.getId());
			em.refresh(fullUser);
			fullUser.getAddressesList().size();
			fullUser.getCardinfoList().size();

			utx.commit();

			session.setAttribute("user", fullUser);

			List<Cart> cartList = fullUser.getCartList();
			if (cartList == null || cartList.isEmpty()) {
				res.sendRedirect(req.getContextPath() + "/products");
				return;
			}

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

			if (removePromo != null && appliedPromo != null && removePromo.equals(appliedPromo.getPromoCode())) {
				session.removeAttribute("appliedPromo");
			}

			if (promoCode != null && !promoCode.isEmpty()) {
				try {
					Promotions promo = em.createQuery(
							"SELECT p FROM Promotions p WHERE p.promoCode = :code AND p.isActive = :isActive AND CURRENT_DATE BETWEEN p.validFrom AND p.validTo",
							Promotions.class)
							.setParameter("code", promoCode)
							.setParameter("isActive", true)
							.getSingleResult();
					session.setAttribute("appliedPromo", promo);
				} catch (Exception e) {
					req.setAttribute("promoCodeError", "Invalid or expired promo code.");
					session.removeAttribute("appliedPromo");
				}
			}

			CartController.calculateOrderSummary(cartList, req, session);

			req.setAttribute("addressList", addressList);
			req.setAttribute("cardList", cardList);

			req.getRequestDispatcher("/checkout.jsp").forward(req, res);
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
			}

			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (!isLoggedIn(req, res, user, "checkout")) return;

		try {
			String addressId = req.getParameter("addressId");
			String paymentMethod = req.getParameter("paymentMethod");
			String cardId = null;
			String cvv = null;

			Boolean hasErrors = false;

			if (addressId == null || addressId.isEmpty()) {
				req.setAttribute("addressSelectorError", "Please select a delivery address.");
				hasErrors = true;
			}

			if (paymentMethod.equals("card")) {
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
				req.setAttribute("selectedPaymentMethod", paymentMethod);
				req.setAttribute("selectedCardId", cardId);
				req.setAttribute("enteredCvv", cvv);

				doGet(req, res);
				return;
			}

			Users fullUser = em.find(Users.class, user.getId());

			Addresses address = em.find(Addresses.class, Integer.parseInt(addressId));
			if (address == null || address.getUserId().getId() != fullUser.getId()) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			Cardinfo card = paymentMethod.equals("card") ? em.find(Cardinfo.class, Integer.parseInt(cardId)) : null;
			if (paymentMethod.equals("card") && (card == null || card.getUserId().getId() != fullUser.getId())) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			List<Cart> cartList = fullUser.getCartList();
			if (cartList == null || cartList.isEmpty()) {
				res.sendRedirect(req.getContextPath() + "/products");
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

			double discount = appliedPromo != null ? subtotal * appliedPromo.getDiscount().doubleValue() : 0;
			double tax = subtotal * 0.06;
			double shipping = subtotal > 1000 ? 0 : 25.00;
			double total = subtotal - discount + tax + shipping;

			utx.begin();

			Orders order = new Orders();
			order.setUserId(fullUser);
			order.setAddressId(address);
			order.setPaymentMethod(paymentMethod);
			order.setCardId(card);
			order.setStatus("packaging");
			order.setTotalPrice(BigDecimal.valueOf(total));
			order.setDeliveryCost(BigDecimal.valueOf(shipping));
			order.setPromoId(appliedPromo);
			order.setDiscount(BigDecimal.valueOf(discount));
			order.setOrderDate(new Date());
			em.persist(order);

			for (Cart item : cartList) {
				Orderdetails orderDetail = new Orderdetails();
				orderDetail.setOrderId(order);
				orderDetail.setProductId(item.getProductId());
				orderDetail.setQuantity(item.getQuantity());
				orderDetail.setPrice(item.getProductId().getPrice());
				em.persist(orderDetail);

				Products product = item.getProductId();
				int newQuantity = product.getStock() - item.getQuantity();

				product.setStock(newQuantity);
				em.merge(product);
			}

			List<Cart> cartItems = em.createQuery("SELECT c FROM Cart c WHERE c.userId = :user", Cart.class)
					.setParameter("user", fullUser)
					.getResultList();

			cartItems.forEach(em::remove);

			utx.commit();

			session.removeAttribute("appliedPromo");
			session.setAttribute("orderSuccess", "true");

			res.sendRedirect(req.getContextPath() + "/user/history");
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception ex) {
			}

			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
