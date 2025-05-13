package Utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileManager {

	private static final String BASE_UPLOAD_DIR = "uploads";
	private static final String AVATARS_DIR = "avatars";
	private static final String PRODUCTS_DIR = "products";

	private static String uploadFile(Part filePart, ServletContext servletContext, String folderType, String oldFilePath) throws IOException {
		if (filePart == null || filePart.getSize() == 0) {
			return oldFilePath;
		}

		String contentType = filePart.getContentType();
		if (!contentType.startsWith("image/")) {
			throw new IOException("Invalid file type. Only images are allowed.");
		}

		String uploadSubdir = folderType.equals(AVATARS_DIR) ? AVATARS_DIR : PRODUCTS_DIR;
		String webAppPath = servletContext.getRealPath("/");
		File webAppDir = new File(webAppPath);
		File projectRootDir = webAppDir.getParentFile().getParentFile();
		File uploadBaseDir = new File(projectRootDir, BASE_UPLOAD_DIR);
		File uploadDir = new File(uploadBaseDir, uploadSubdir);

		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		String extension = contentType.split("/")[1];
		String fileName = UUID.randomUUID() + "." + extension;

		if (oldFilePath != null && !oldFilePath.isEmpty()) {
			String oldFileName = oldFilePath.substring(oldFilePath.lastIndexOf("/") + 1);
			File oldFile = new File(uploadDir, oldFileName);

			if (oldFile.exists()) {
				try {
					Files.delete(oldFile.toPath());
				} catch (Exception e) {
					Logger.error("Failed to delete old file: " + oldFile.getPath(), e);
				}
			}
		}

		File file = new File(uploadDir, fileName);
		try (InputStream input = filePart.getInputStream()) {
			Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.info("File uploaded successfully: " + file.getPath());
		}

		return uploadSubdir + "/" + fileName;
	}

	public static String uploadAvatar(Part filePart, ServletContext servletContext, String oldAvatarPath) throws IOException {
		return uploadFile(filePart, servletContext, AVATARS_DIR, oldAvatarPath);
	}

	public static String uploadProductImage(Part filePart, ServletContext servletContext, String oldImagePath) throws IOException {
		String filePath = uploadFile(filePart, servletContext, PRODUCTS_DIR, oldImagePath);

		if (filePath != null && !filePath.equals(oldImagePath)) {
			return "uploads/" + filePath;
		}

		return oldImagePath;
	}

}
