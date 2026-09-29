package web.programming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {

	@NotBlank(message = "Email khong duoc de trong")
	@Email(message = "Email khong hop le")
	private String email;

	@NotBlank(message = "Mat khau moi khong duoc de trong")
	@Size(min = 6, message = "Mat khau toi thieu 6 ky tu")
	private String password;

	@NotBlank(message = "Vui long xac nhan mat khau")
	private String confirmPassword;
}
