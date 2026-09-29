package web.programming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import web.programming.dto.ForgotPasswordDTO;
import web.programming.dto.RegisterDTO;
import web.programming.dto.ResetPasswordDTO;
import web.programming.dto.VerifyOtpDTO;
import web.programming.service.AuthService;
import web.programming.service.OtpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Xac thuc:
 * - Dang nhap: Spring Security xu ly (POST /login), chi can GET /login tra ve view.
 * - Dang ky + xac nhan OTP qua mail + gui lai OTP.
 * - Quen mat khau: gui OTP -> xac nhan OTP -> doi mat khau.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final OtpService otpService;

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("registerDTO", new RegisterDTO());
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "auth/register";
		}
		try {
			authService.register(dto);
			redirect.addFlashAttribute("success",
					"OTP da duoc gui den email " + dto.getEmail() + ". Vui long kiem tra hop thu.");
			return "redirect:/verify-otp?email=" + dto.getEmail();
		} catch (IllegalArgumentException | IllegalStateException e) {
			result.reject("register.error", e.getMessage());
			return "auth/register";
		}
	}

	@GetMapping("/verify-otp")
	public String verifyPage(@RequestParam(required = false) String email, Model model) {
		VerifyOtpDTO dto = new VerifyOtpDTO();
		dto.setEmail(email);
		model.addAttribute("verifyOtpDTO", dto);
		return "auth/verify-otp";
	}

	@PostMapping("/verify-otp")
	public String verify(@Valid @ModelAttribute("verifyOtpDTO") VerifyOtpDTO dto, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "auth/verify-otp";
		}
		if (!authService.verifyRegister(dto.getEmail(), dto.getOtp())) {
			result.reject("otp.error", "OTP khong hop le, het han hoac da qua so lan thu.");
			return "auth/verify-otp";
		}
		redirect.addFlashAttribute("success", "Xac nhan tai khoan thanh cong. Hay dang nhap.");
		return "redirect:/login";
	}

	@PostMapping("/resend-register-otp")
	public String resend(@RequestParam String email, RedirectAttributes redirect) {
		try {
			otpService.sendRegisterOtp(email);
			redirect.addFlashAttribute("success", "Da gui lai OTP den email " + email + ".");
		} catch (IllegalStateException e) {
			redirect.addFlashAttribute("flashError", e.getMessage());
		}
		return "redirect:/verify-otp?email=" + email;
	}

	@GetMapping("/forgot-password")
	public String forgotForm(Model model) {
		model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
		return "auth/forgot-password";
	}

	@PostMapping("/forgot-password")
	public String forgot(@Valid @ModelAttribute("forgotPasswordDTO") ForgotPasswordDTO dto, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "auth/forgot-password";
		}
		try {
			authService.forgotPassword(dto.getEmail());
			redirect.addFlashAttribute("email", dto.getEmail());
			redirect.addFlashAttribute("success", "OTP dat lai mat khau da duoc gui den " + dto.getEmail() + ".");
			return "redirect:/reset-password";
		} catch (IllegalArgumentException | IllegalStateException e) {
			result.reject("forgot.error", e.getMessage());
			return "auth/forgot-password";
		}
	}

	@GetMapping("/reset-password")
	public String resetForm(Model model) {
		ResetPasswordDTO dto = new ResetPasswordDTO();
		Object email = model.asMap().get("email");
		if (email != null) {
			dto.setEmail(email.toString());
		}
		model.addAttribute("resetPasswordDTO", dto);
		return "auth/reset-password";
	}

	@PostMapping("/reset-password")
	public String reset(@Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO dto, BindingResult result,
			@RequestParam(required = false) String otp, RedirectAttributes redirect) {
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			result.reject("password.error", "Mat khau xac nhan khong dung.");
		}
		if (otp == null || otp.isBlank()) {
			result.reject("otp.error", "Vui long nhap OTP.");
		}
		if (result.hasErrors()) {
			return "auth/reset-password";
		}
		if (!authService.verifyResetOtp(dto.getEmail(), otp)) {
			result.reject("otp.error", "OTP khong hop le hoac da het han.");
			return "auth/reset-password";
		}
		authService.resetPassword(dto.getEmail(), dto.getPassword());
		redirect.addFlashAttribute("success", "Doi mat khau thanh cong. Hay dang nhap lai.");
		return "redirect:/login";
	}
}
