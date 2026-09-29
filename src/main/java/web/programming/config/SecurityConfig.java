package web.programming.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * Cau hinh Spring Security 7:
 * - Dang nhap bang form login + session (JSESSIONID)
 * - Mat khau ma hoa BCrypt
 * - Phan quyen: /users/** chi ROLE_ADMIN; /products/** da dang nhap;
 *   cac trang auth + file tinh de cong khai.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider)
			throws Exception {

		http.authenticationProvider(authenticationProvider)
				.authorizeHttpRequests(auth -> auth
						// Cong khai: trang chu, dang ky/dang nhap/OTP/quen mat khau, file tinh
						.requestMatchers("/", "/home", "/login", "/register", "/verify-otp", "/resend-register-otp",
								"/forgot-password", "/reset-password",
								"/assets/**", "/image/**", "/favicon.ico", "/error")
						.permitAll()
						// Quan tri nguoi dung: chi ADMIN
						.requestMatchers("/users/**").hasRole("ADMIN")
						// Quan ly san pham (CRUD): da dang nhap
						.requestMatchers("/products/**").authenticated()
						// Con lai: da dang nhap
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/", true)
						.failureUrl("/login?error=true")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll())
				.sessionManagement(session -> session.maximumSessions(1).maxSessionsPreventsLogin(false));

		return http.build();
	}
}
