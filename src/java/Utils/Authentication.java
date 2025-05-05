/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import Model.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author zhon12345
 */
public class Authentication {

	public static boolean isAuthorized(Users user) {
		return user != null && (user.getRole().equalsIgnoreCase("staff") || user.getRole().equalsIgnoreCase("manager"));
	}

	public static boolean isLoggedIn(HttpServletRequest req, HttpServletResponse res, Users user, String redirect, boolean checkAuth) throws UnsupportedEncodingException, IOException {
		if (user == null) {
			String loginString = req.getContextPath() + "/login";

			if (redirect != null && !redirect.isEmpty()) {
				loginString += "?redirect=" + URLEncoder.encode(redirect, "UTF-8");
			}

			res.sendRedirect(loginString);
			return false;
		}

		if (checkAuth && !isAuthorized(user)) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}

		return true;
	}

	public static boolean isLoggedIn(HttpServletRequest req, HttpServletResponse res, Users user, String redirect) {
		try {
			return isLoggedIn(req, res, user, redirect, false);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isLoggedInAndAuthorized(HttpServletRequest req, HttpServletResponse res, Users user, String redirect) throws IOException {
		return isLoggedIn(req, res, user, redirect, true);
	}

	public static String hashPassword(String password) throws ServletException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes());

			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException(e);
		}
	}

}
