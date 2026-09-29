package web.programming.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.programming.dto.RegisterDTO;
import web.programming.entity.Role;
import web.programming.entity.User;
import web.programming.repository.RoleRepository;
import web.programming.repository.UserRepository;
import web.programming.service.AuthService;
import web.programming.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final OtpService otpService;

	@Override
	@Transactional
	public void register(RegisterDTO dto) {
		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new IllegalArgumentException("Username da ton tai");
		}
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new IllegalArgumentException("Email da ton tai");
		}
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			throw new IllegalArgumentException("Mat khau xac nhan khong dung");
		}

		Role role = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new IllegalStateException("Chua co ROLE_USER trong database"));

		User user = User.builder()
				.username(dto.getUsername())
				.email(dto.getEmail())
				.password(passwordEncoder.encode(dto.getPassword()))
				.fullName(dto.getFullName())
				.enabled(false)
				.role(role)
				.createdAt(LocalDateTime.now())
				.build();
		userRepository.save(user);

		// Gui OTP xac nhan dang ky
		otpService.sendRegisterOtp(dto.getEmail());
	}

	@Override
	@Transactional
	public boolean verifyRegister(String email, String otp) {
		boolean ok = otpService.verifyRegisterOtp(email, otp);
		if (!ok) {
			return false;
		}
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User khong ton tai"));
		user.setEnabled(true);
		return true;
	}

	@Override
	@Transactional
	public void forgotPassword(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email khong ton tai trong he thong");
		}
		otpService.sendResetPasswordOtp(email);
	}

	@Override
	public boolean verifyResetOtp(String email, String otp) {
		return otpService.verifyResetPasswordOtp(email, otp);
	}

	@Override
	@Transactional
	public void resetPassword(String email, String password) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("Email khong ton tai"));
		user.setPassword(passwordEncoder.encode(password));
	}
}
