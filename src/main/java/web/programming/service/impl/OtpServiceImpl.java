package web.programming.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.programming.entity.OtpToken;
import web.programming.repository.OtpTokenRepository;
import web.programming.service.EmailService;
import web.programming.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

	private static final int MAX_ATTEMPTS = 5;
	private static final int OTP_MINUTES = 5;
	private static final int RESEND_COOLDOWN_SECONDS = 30;

	private final OtpTokenRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final SecureRandom random = new SecureRandom();

	private String generateOtp() {
		return "%06d".formatted(random.nextInt(1_000_000));
	}

	private void send(String email, String type, String subject) {
		// Chan gui lai qua nhanh (spam)
		repository.findTopByEmailAndTypeOrderByCreatedAtDesc(email, type).ifPresent(last -> {
			if (last.getCreatedAt() != null
					&& last.getCreatedAt().plusSeconds(RESEND_COOLDOWN_SECONDS).isAfter(LocalDateTime.now())) {
				throw new IllegalStateException(
						"Vui long doi " + RESEND_COOLDOWN_SECONDS + " giay truoc khi gui lai OTP");
			}
		});

		// Xoa OTP cu, tao OTP moi
		repository.deleteByEmailAndType(email, type);
		String otp = generateOtp();
		OtpToken token = OtpToken.builder()
				.email(email)
				.otpHash(passwordEncoder.encode(otp))
				.type(type)
				.expiresAt(LocalDateTime.now().plusMinutes(OTP_MINUTES))
				.attempts(0)
				.used(false)
				.createdAt(LocalDateTime.now())
				.build();
		repository.save(token);

		emailService.sendOtp(email, otp, subject);
	}

	@Override
	@Transactional
	public void sendRegisterOtp(String email) {
		send(email, "REGISTER", "Shop - Xac nhan dang ky tai khoan");
	}

	@Override
	@Transactional
	public void sendResetPasswordOtp(String email) {
		send(email, "RESET_PASSWORD", "Shop - OTP dat lai mat khau");
	}

	private boolean verify(String email, String otp, String type) {
		OtpToken token = repository.findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, type).orElse(null);
		if (token == null || token.getExpiresAt().isBefore(LocalDateTime.now())
				|| token.getAttempts() >= MAX_ATTEMPTS) {
			return false;
		}

		token.setAttempts(token.getAttempts() + 1);
		if (!passwordEncoder.matches(otp, token.getOtpHash())) {
			repository.save(token);
			return false;
		}

		token.setUsed(true);
		repository.save(token);
		return true;
	}

	@Override
	@Transactional
	public boolean verifyRegisterOtp(String email, String otp) {
		return verify(email, otp, "REGISTER");
	}

	@Override
	@Transactional
	public boolean verifyResetPasswordOtp(String email, String otp) {
		return verify(email, otp, "RESET_PASSWORD");
	}
}
