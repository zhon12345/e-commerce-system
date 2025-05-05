package Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UploadsController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is missing.");
			return;
		}

		String filePath = pathInfo.substring(1);
		if (filePath.contains("..") || filePath.contains("://")) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid file path.");
			return;
		}

		String webAppPath = getServletContext().getRealPath("/");
		File webAppDir = new File(webAppPath);
		File projectRootDir = webAppDir.getParentFile().getParentFile();
		File uploadDir = new File(projectRootDir, "uploads");
		File file = new File(uploadDir, filePath);

		if (!file.exists() || !file.isFile()) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found.");
			return;
		}

		String contentType = getServletContext().getMimeType(file.getName());
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
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
}