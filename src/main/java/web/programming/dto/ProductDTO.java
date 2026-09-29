package web.programming.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO san pham (map voi entity Product bang MapStruct).
 */
@Data
public class ProductDTO {

	private String id;

	@NotBlank(message = "Ten san pham khong duoc de trong")
	@Size(max = 255, message = "Ten san pham toi da 255 ky tu")
	private String name;

	@Size(max = 4000, message = "Mo ta toi da 4000 ky tu")
	private String description;

	@NotNull(message = "Gia khong duoc de trong")
	@DecimalMin(value = "0.0", message = "Gia phai >= 0")
	private Double price;

	/** "<url>|<publicId>" neu upload Cloudinary, hoac "/image/..." (local fallback) */
	private String imageUrl;

	/** Nguoi so huu san pham */
	private Long userId;

	private String username;

	/** File anh upload tu form (khong map vao entity) */
	private MultipartFile image;

	/** URL hien thi anh (dung trong view) */
	public String getDisplayImageUrl() {
		if (imageUrl == null || imageUrl.isBlank()) {
			return null;
		}
		String url = imageUrl.contains("|") ? imageUrl.substring(0, imageUrl.indexOf('|')) : imageUrl;
		if (url.startsWith("http") || url.startsWith("/")) {
			return url;
		}
		return "/image/" + url;
	}
}
