package Controller;

import java.io.IOException;
import java.util.List;

import Model.Addresses;
import Model.Users;
import static Utils.Authentication.isLoggedIn;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AddressController", urlPatterns = { "/user/address" })
public class AddressController extends BaseController {
	
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

		if (!isLoggedIn(req, res, user, "address")) return;

		String action = req.getParameter("action");

		if ("update".equals(action)) {
			try {
				int addressId = Integer.parseInt(req.getParameter("addressId"));
				Addresses address = em.find(Addresses.class, addressId);

				if (address == null || address.getUserId().getId() != user.getId()) {
					res.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}

				req.setAttribute("updateAddress", address);
				req.setAttribute("name", address.getReceiverName());
				req.setAttribute("phone", address.getContactNumber());
				req.setAttribute("line1", address.getAddress1());
				req.setAttribute("line2", address.getAddress2());
				req.setAttribute("postcode", address.getPostalCode());
				req.setAttribute("city", address.getCity());
				req.setAttribute("state", address.getState());

			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		loadAddresses(req, user);
		forwardToPage(req, res, "/user/address.jsp");
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

		if (!isLoggedIn(req, res, user, "address")) return;

		String action = req.getParameter("action");

		switch (action) {
			case "create":
				createAddress(req, res, user);
				break;
			case "update":
			case "delete":
				try {
					int addressId = Integer.parseInt(req.getParameter("addressId"));
					Addresses address = em.find(Addresses.class, addressId);

					if (address == null || address.getUserId().getId() != user.getId()) {
						setErrorMessage(req, "Address not found");
						redirectToPage(req, res, "/user/address");
						return;
					}

					if (action.equals("update")) {
						updateAddress(req, res, user, address);
					}

					if (action.equals("delete")) {
						deleteAddress(req, address);
					}
				} catch (NumberFormatException e) {
					setErrorMessage(req, "Address not found");
				}
		}

		redirectToPage(req, res, "/user/address");
	}

	private void createAddress(HttpServletRequest req, HttpServletResponse res, Users user) throws ServletException, IOException {
		Addresses address = new Addresses();
		boolean success = processAddressData(req, address);

		if (!success) {
			loadAddresses(req, user);
			forwardToPage(req, res, "/user/address.jsp");
			return;
		}

		handleTransaction(() -> {
			address.setUserId(user);
			em.persist(address);
		}, req, "Address added successfully!", "Error adding address");
	}

	private void updateAddress(HttpServletRequest req, HttpServletResponse res, Users user, Addresses address) throws ServletException, IOException {
		boolean success = processAddressData(req, address);

		if (!success) {
			loadAddresses(req, user);
			forwardToPage(req, res, "/user/address.jsp");
			return;
		}

		handleTransaction(() -> {
			em.merge(address);
		}, req, "Address updated successfully!", "Error updating address");
	}

	private void deleteAddress(HttpServletRequest req, Addresses address) {
		handleTransaction(() -> {
			address.setIsArchived(true);
			em.merge(address);
		}, req, "Address deleted successfully!", "Error deleting address");
	}

	private boolean processAddressData(HttpServletRequest req, Addresses address) {
		String name = req.getParameter("name").trim();
		String phone = req.getParameter("phone").trim();
		String line1 = req.getParameter("line1").trim();
		String line2 = req.getParameter("line2") != null ? req.getParameter("line2").trim() : "";
		String postcode = req.getParameter("postcode").trim();
		String city = req.getParameter("city").trim();
		String state = req.getParameter("state").trim();

		Boolean hasErrors = false;

		if (name.isEmpty()) {
			req.setAttribute("nameError", "Receiver name is required");
			hasErrors = true;
		}

		if (phone.isEmpty()) {
			req.setAttribute("phoneError", "Phone is required");
			hasErrors = true;
		}

		if (line1.isEmpty()) {
			req.setAttribute("line1Error", "Address Line 1 is required");
			hasErrors = true;
		}

		if (postcode.isEmpty()) {
			req.setAttribute("postcodeError", "Postcode is required");
			hasErrors = true;
		}

		if (city.isEmpty()) {
			req.setAttribute("cityError", "City is required");
			hasErrors = true;
		}

		if (state.isEmpty()) {
			req.setAttribute("stateError", "State is required");
			hasErrors = true;
		}

		if (hasErrors) {
			req.setAttribute("name", name);
			req.setAttribute("phone", phone);
			req.setAttribute("line1", line1);
			req.setAttribute("line2", line2);
			req.setAttribute("city", city);
			req.setAttribute("state", state);
			req.setAttribute("postcode", postcode);
			return false;
		}

		address.setReceiverName(name);
		address.setContactNumber(phone);
		address.setAddress1(line1);
		address.setAddress2(line2);
		address.setCity(city);
		address.setPostalCode(postcode);
		address.setState(state);
		return true;
	}

	private void loadAddresses(HttpServletRequest req, Users user) {
		List<Addresses> addresses = em.createQuery(
				"SELECT c FROM Addresses c WHERE c.userId = :user AND c.isArchived = :isArchived",
				Addresses.class)
				.setParameter("user", user)
				.setParameter("isArchived", false)
				.getResultList();

		req.setAttribute("addresses", addresses);
	}
}
