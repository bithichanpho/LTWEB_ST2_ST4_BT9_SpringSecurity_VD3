package web.programming.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpDTO {

	@NotBlank(message = "Email khong duoc de trong")
	@Email(message = "Email khong hop le")
	private String email;

	@NotBlank(message = "Vui long nhap OTP")
	@Size(min = 6, max = 6, message = "OTP gom 6 chu so")
	private String otp;
}
