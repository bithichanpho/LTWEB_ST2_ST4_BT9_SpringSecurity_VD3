package web.programming.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * San pham cua cua hang. San pham thuoc ve 1 nguoi dung (user_id).
 * Cot images luu duong dan anh:
 *   - Cloudinary: "<secure_url>|<public_id>"
 *   - Local fallback: "/image/products/xxx.png|local:image/products/xxx.png"
 *   - Du lieu cu: "products/xxx.png" (hien thi qua /image/**).
 */
@Entity
@Table(name = "products", indexes = {
		@Index(name = "idx_products_name", columnList = "product_name") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@Column(name = "product_id", length = 10)
	private String productId;

	@Column(name = "product_name", columnDefinition = "NVARCHAR(255) NULL")
	private String productName;

	@Column(name = "price")
	private double price;

	@Column(name = "description", columnDefinition = "NVARCHAR(4000) NULL")
	private String description;

	@Column(name = "images", columnDefinition = "NVARCHAR(1000) NULL")
	private String images;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	/** Chu so huu san pham (quan he 1 user - n product) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	/**
	 * URL hien thi anh (dung trong view):
	 * - "<url>|<publicId>" (Cloudinary) -> tra ve phan url
	 * - "products/x.png" -> "/image/products/x.png"
	 * - "/image/..." hoac "http..." -> giu nguyen
	 */
	public String getDisplayImageUrl() {
		if (images == null || images.isBlank()) {
			return null;
		}
		String url = images.contains("|") ? images.substring(0, images.indexOf('|')) : images;
		if (url.startsWith("http") || url.startsWith("/")) {
			return url;
		}
		return "/image/" + url;
	}
}
