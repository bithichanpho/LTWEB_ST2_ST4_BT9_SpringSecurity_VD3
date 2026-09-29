package web.programming.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;
import web.programming.config.CloudinaryConfig;
import web.programming.service.CloudinaryService;
import web.programming.service.CloudinaryUploadResult;

/**
 * Upload anh len Cloudinary (folder: shop/products).
 * Neu chua cau hinh Cloudinary: luu local vao app.upload.dir/image/products
 * (duoc phuc vu qua /image/** - xem spring.web.resources.static-locations)
 * va tra ve publicId dang "local:..." de co the xoa sau nay.
 */
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

	private static final Logger log = LoggerFactory.getLogger(CloudinaryServiceImpl.class);

	private final Cloudinary cloudinary;
	private final CloudinaryConfig cloudinaryConfig;

	@Value("${app.upload.dir}")
	private String uploadDir;

	@Override
	public CloudinaryUploadResult upload(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Chua chon anh");
		}
		String type = file.getContentType();
		if (type == null || !type.startsWith("image/")) {
			throw new IllegalArgumentException("Chi cho phep file hinh anh");
		}

		if (!cloudinaryConfig.isConfigured()) {
			// Fallback: chua cau hinh Cloudinary -> luu local de project van chay duoc
			try {
				String relativePath = storeLocal(file, "image/products");
				return new CloudinaryUploadResult("/" + relativePath, "local:" + relativePath);
			} catch (IOException e) {
				throw new IllegalStateException("Upload anh that bai", e);
			}
		}

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().upload(
					file.getBytes(),
					Map.of("folder", "shop/products"));
			return new CloudinaryUploadResult(
					String.valueOf(result.get("secure_url")),
					String.valueOf(result.get("public_id")));
		} catch (Exception e) {
			throw new IllegalStateException("Upload Cloudinary that bai: " + e.getMessage(), e);
		}
	}

	@Override
	public void delete(String publicId) {
		if (publicId == null || publicId.isBlank()) {
			return;
		}

		// Anh luu local (fallback)
		if (publicId.startsWith("local:")) {
			deleteLocal(publicId.substring("local:".length()));
			return;
		}

		if (!cloudinaryConfig.isConfigured()) {
			return;
		}

		try {
			cloudinary.uploader().destroy(publicId, Map.of("resource_type", "image"));
		} catch (Exception e) {
			log.warn("[CLOUDINARY] Xoa anh that bai ({}): {}", publicId, e.getMessage());
		}
	}

	/** Luu file vao app.upload.dir/<subFolder>/<ten file>, tra ve duong dan tuong doi */
	private String storeLocal(MultipartFile file, String subFolder) throws IOException {
		Path targetDir = Path.of(uploadDir, subFolder);
		Files.createDirectories(targetDir);

		String originalName = Path.of(file.getOriginalFilename()).getFileName().toString();
		String fileName = System.currentTimeMillis() + "_" + originalName;

		Path target = targetDir.resolve(fileName);
		Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
		return subFolder + "/" + fileName;
	}

	/** Xoa file local, chan path traversal */
	private void deleteLocal(String relativePath) {
		try {
			Path root = Path.of(uploadDir).toAbsolutePath().normalize();
			Path target = root.resolve(relativePath).normalize();
			if (!target.startsWith(root)) {
				return;
			}
			Files.deleteIfExists(target);
		} catch (IOException e) {
			log.warn("Khong the xoa file upload: {}", relativePath, e);
		}
	}
}
