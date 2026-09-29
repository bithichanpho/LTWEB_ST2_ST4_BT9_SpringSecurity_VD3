package web.programming.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

/**
 * Cau hinh Cloudinary de upload anh san pham len cloud.
 * Cac gia tri lay tu file .env: CLOUDINARY_CLOUD_NAME / CLOUDINARY_API_KEY / CLOUDINARY_API_SECRET.
 * Neu chua cau hinh, ung dung tu dong luu anh vao thu muc local (xem CloudinaryServiceImpl).
 */
@Configuration
public class CloudinaryConfig {

	@Value("${cloudinary.cloud-name:}")
	private String cloudName;

	@Value("${cloudinary.api-key:}")
	private String apiKey;

	@Value("${cloudinary.api-secret:}")
	private String apiSecret;

	/** Da cau hinh du 3 gia tri Cloudinary chua? */
	public boolean isConfigured() {
		return cloudName != null && !cloudName.isBlank()
				&& apiKey != null && !apiKey.isBlank()
				&& apiSecret != null && !apiSecret.isBlank();
	}

	@Bean
	public Cloudinary cloudinary() {
		Map<String, String> config = new HashMap<>();
		config.put("cloud_name", isConfigured() ? cloudName : "demo");
		config.put("api_key", apiKey == null ? "" : apiKey);
		config.put("api_secret", apiSecret == null ? "" : apiSecret);
		return new Cloudinary(config);
	}
}
