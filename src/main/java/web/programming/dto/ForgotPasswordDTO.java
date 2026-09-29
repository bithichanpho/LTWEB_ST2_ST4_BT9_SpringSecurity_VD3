package web.programming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDTO {

	@NotBlank(message = "Email khong duoc de trong")
	@Email(message = "Email khong hop le")
	private String email;
}
