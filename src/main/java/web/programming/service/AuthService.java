package web.programming.service;

import web.programming.dto.RegisterDTO;

/**
 * Nghiep vu xac thuc: dang ky + OTP, quen mat khau + OTP.
 */
public interface AuthService {

	/** Dang ky tai khoan (trang thai chua kich hoat) va gui OTP xac nhan qua email */
	void register(RegisterDTO dto);

	/** Xac nhan OTP dang ky -> kich hoat tai khoan */
	boolean verifyRegister(String email, String otp);

	/** Gui OTP dat lai mat khau */
	void forgotPassword(String email);

	/** Kiem tra OTP dat lai mat khau */
	boolean verifyResetOtp(String email, String otp);

	/** Doi mat khau moi */
	void resetPassword(String email, String password);
}
