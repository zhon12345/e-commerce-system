/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.user;

import java.io.IOException;
import java.util.List;

import Model.Addresses;
import Model.Users;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;

/**
 *
 * @author zhon12345
 */
public class Address extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

	@Resource
	private UserTransaction utx;

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
		HttpSession session = req.getSession();
		Users user = (Users) session.getAttribute("user");

		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp");
			return;
		}

		String action = req.getParameter("action");
		if (action != null) {
			try {
				int id = Integer.parseInt(req.getParameter("id"));
				Addresses address = em.find(Addresses.class, id);

				if (address == null || address.getUserId().getId() != user.getId()) {
					res.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}

				switch (action) {
					case "delete":
						try {
							utx.begin();

							address.setIsArchived(true);
							em.merge(address);
							utx.commit();

							session.setAttribute("deleteSuccess", "true");
						} catch (Exception e) {
							try {
								utx.rollback();
							} catch (Exception ex) {
							}

							loadAddresses(req, user);
						}

						res.sendRedirect(req.getContextPath() + "/user/address");
                                    return;

					case "edit":
						req.setAttribute("editAddress", address);
						req.setAttribute("name", address.getReceiverName());
						req.setAttribute("phone", address.getContactNumber());
						req.setAttribute("line1", address.getAddress1());
						req.setAttribute("line2", address.getAddress2());
						req.setAttribute("postcode", address.getPostalCode());
						req.setAttribute("city", address.getCity());
						req.setAttribute("state", address.getState());
						break;
				}
			} catch (Exception e) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}

		loadAddresses(req, user);

            req.getRequestDispatcher("/user/address.jsp").forward(req, res);

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

		if (user == null) {
			res.sendRedirect(req.getContextPath() + "/login.jsp");
			return;
		}

		String name = req.getParameter("name").trim();
		String phone = req.getParameter("phone").trim();
		String line1 = req.getParameter("line1").trim();
		String line2 = req.getParameter("line2") != null ? req.getParameter("line2").trim() : "";
		String postcode = req.getParameter("postcode").trim();
		String city = req.getParameter("city").trim();
		String state = req.getParameter("state").trim();
		String action = req.getParameter("action");

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

			loadAddresses(req, user);

			req.getRequestDispatcher("/user/address.jsp").forward(req, res);
			return;
		}

		try {
			utx.begin();

			Addresses address;
			if ("edit".equals(action)) {
				int id = Integer.parseInt(req.getParameter("id"));
				address = em.find(Addresses.class, id);

				if (address == null || address.getUserId().getId() != user.getId()) {
					res.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			} else {
				address = new Addresses();
				address.setUserId(user);
			}

			address.setReceiverName(name);
			address.setContactNumber(phone);
			address.setAddress1(line1);
			address.setAddress2(line2);
			address.setCity(city);
			address.setPostalCode(postcode);
			address.setState(state);

			if ("edit".equals(action)) {
				em.merge(address);
			} else {
				em.persist(address);
			}

			utx.commit();

			if ("edit".equals(action)) {
				session.setAttribute("editSuccess", "true");
			} else {
				session.setAttribute("addSuccess", "true");
			}

			res.sendRedirect(req.getContextPath() + "/user/address");
		} catch (Exception e) {
			try {
				if (utx != null)
					utx.rollback();
			} catch (Exception ex) {
			}

			req.setAttribute("error", "Failed to save address: " + e.getMessage());
			req.getRequestDispatcher("/user/address.jsp").forward(req, res);
		}
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
