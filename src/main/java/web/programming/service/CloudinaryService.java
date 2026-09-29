package web.programming.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Upload / xoa anh tren Cloudinary.
 * Khi chua cau hinh Cloudinary, tu dong luu file vao thu muc local (xem README).
 */
public interface CloudinaryService {

	CloudinaryUploadResult upload(MultipartFile file);

	void delete(String publicId);
}
