package web.programming.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Ep toan bo request/response dung UTF-8 (tieng Viet hien thi dung tren moi trinh duyet).
 */
@Configuration
public class EncodingConfig {

	@Bean
	public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setOrder(Integer.MIN_VALUE);
		return registration;
	}
}
