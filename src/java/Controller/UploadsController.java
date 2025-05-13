package Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UploadsController", urlPatterns = { "/uploads/*" })
public class UploadsController extends BaseController {

	/**
	 *
	 * @param req servlet request
	 * @param res servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			sendError(req, res, HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String filePath = pathInfo.substring(1);
		if (filePath.contains("..") || filePath.contains("://")) {
			sendError(req, res, HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String webAppPath = req.getServletContext().getRealPath("/");
		File webAppDir = new File(webAppPath);
		File projectRootDir = webAppDir.getParentFile().getParentFile();
		File uploadDir = new File(projectRootDir, "uploads");
		File file = new File(uploadDir, filePath);

		if (!file.exists() || !file.isFile()) {
			sendError(req, res, HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String contentType = req.getServletContext().getMimeType(file.getName());

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		res.setContentType(contentType);

		if (contentType.startsWith("image/")) {
			res.setHeader("Cache-Control", "public, max-age=86400");
		}

		try {
			Files.copy(file.toPath(), res.getOutputStream());
		} catch (IOException e) {
			handleException(req, res, e);
		}
	}

	/**
	 *
	 * @param req servlet request
	 * @param res servlet response
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		sendError(req, res, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
}