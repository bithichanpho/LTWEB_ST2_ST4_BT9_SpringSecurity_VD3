package web.programming.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import web.programming.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	@Override
	public void sendOtp(String email, String otp, String subject) {
		String content = """
				Xin chao,
				Ma OTP cua ban la: %s
				OTP co hieu luc trong 5 phut va chi su dung mot lan.
				Khong chia se ma nay cho nguoi khac.
				""".formatted(otp);

		// Chua cau hinh SMTP -> che do dev: in OTP ra console de demo
		if (mailUsername == null || mailUsername.isBlank()) {
			log.warn("[DEV-MAIL] Chua cau hinh SMTP (MAIL_USERNAME trong .env). OTP cho {} [{}]: {}",
					email, subject, otp);
			return;
		}

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setSubject(subject);
			message.setText(content);
			mailSender.send(message);
			log.info("[MAIL] Da gui OTP toi {}", email);
		} catch (Exception e) {
			log.error("[MAIL] Gui OTP that bai toi {}: {}", email, e.getMessage());
			log.warn("[DEV-MAIL] OTP cho {} [{}]: {}", email, subject, otp);
		}
	}
}
