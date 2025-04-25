package Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FetchUploads extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is missing.");
			return;
		}

		String fileName = pathInfo.substring(1);

		String webAppPath = getServletContext().getRealPath("/");
		File webAppDir = new File(webAppPath);
		File projectRootDir = webAppDir.getParentFile().getParentFile();
		File uploadDir = new File(projectRootDir, "uploads/avatars");

		if (!uploadDir.exists()) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "Upload directory not found.");
			return;
		}

		File file = new File(uploadDir, fileName);
		if (!file.exists()) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found.");
			return;
		}

		String contentType = getServletContext().getMimeType(file.getName());
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		res.setContentType(contentType);

		try {
			Files.copy(file.toPath(), res.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to stream file.");
		}
	}
}