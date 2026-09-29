package web.programming.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import web.programming.entity.User;
import web.programming.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Cho phep dang nhap bang username HOAC email.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameOrEmail(login, login)
				.orElseThrow(() -> new UsernameNotFoundException("Khong tim thay tai khoan: " + login));
		return new CustomUserDetails(user);
	}
}
