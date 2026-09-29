package web.programming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO nguoi dung (map voi entity User bang MapStruct).
 */
@Data
public class UserDTO {

	private Long id;

	@NotBlank(message = "Username khong duoc de trong")
	@Size(max = 50, message = "Username toi da 50 ky tu")
	private String username;

	@NotBlank(message = "Email khong duoc de trong")
	@Email(message = "Email khong hop le")
	private String email;

	@NotBlank(message = "Ho ten khong duoc de trong")
	private String fullName;

	private boolean enabled;

	private String roleName;

	/** So san pham cua nguoi dung (yeu cau: dem product cua tung user) */
	private long productCount;
}
