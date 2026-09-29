package web.programming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

	@NotBlank(message = "Username khong duoc de trong")
	@Size(min = 3, max = 50, message = "Username phai tu 3 den 50 ky tu")
	private String username;

	@NotBlank(message = "Email khong duoc de trong")
	@Email(message = "Email khong hop le")
	private String email;

	@NotBlank(message = "Mat khau khong duoc de trong")
	@Size(min = 6, message = "Mat khau toi thieu 6 ky tu")
	private String password;

	@NotBlank(message = "Vui long xac nhan mat khau")
	private String confirmPassword;

	@NotBlank(message = "Ho ten khong duoc de trong")
	private String fullName;
}
