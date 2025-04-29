/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.Promotions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author yjee0
 */
public class IndexController extends HttpServlet {

	@PersistenceContext
	private EntityManager em;

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

		try {
			List<Promotions> promoList = em.createQuery(
					"SELECT p FROM Promotions p WHERE p.isActive = :isActive AND CURRENT_DATE BETWEEN p.validFrom AND p.validTo",
					Promotions.class)
					.setParameter("isActive", true)
					.getResultList();

			req.setAttribute("promotions", promoList);
			req.getRequestDispatcher("/index.jsp").forward(req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
