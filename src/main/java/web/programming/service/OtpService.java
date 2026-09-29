package web.programming.service;

/**
 * Sinh + gui + xac thuc OTP (luu hash, co thoi han, gioi han so lan thu).
 */
public interface OtpService {

	void sendRegisterOtp(String email);

	boolean verifyRegisterOtp(String email, String otp);

	void sendResetPasswordOtp(String email);

	boolean verifyResetPasswordOtp(String email, String otp);
}
