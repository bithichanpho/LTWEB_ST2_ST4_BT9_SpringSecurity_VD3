package web.programming.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import web.programming.dto.UserDTO;
import web.programming.entity.Role;
import web.programming.entity.User;
import web.programming.mapper.UserMapper;
import web.programming.repository.RoleRepository;
import web.programming.repository.UserRepository;
import web.programming.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	/** Mat khau mac dinh khi admin tao user moi (theo huong dan) */
	private static final String DEFAULT_PASSWORD = "123456";

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper mapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public Page<UserDTO> findAll(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "id"));
		return userRepository.search(keyword == null ? "" : keyword, pageable).map(user -> {
			UserDTO dto = mapper.toDTO(user);
			dto.setProductCount(userRepository.countProductsByUserId(user.getId()));
			return dto;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User khong ton tai"));
		UserDTO dto = mapper.toDTO(user);
		dto.setProductCount(userRepository.countProductsByUserId(id));
		return dto;
	}

	@Override
	@Transactional
	public UserDTO create(UserDTO dto) {
		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new IllegalArgumentException("Username da ton tai");
		}
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new IllegalArgumentException("Email da ton tai");
		}

		User user = mapper.toEntity(dto);
		String roleName = (dto.getRoleName() == null || dto.getRoleName().isBlank())
				? "ROLE_USER"
				: dto.getRoleName();
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new IllegalArgumentException("Role khong ton tai: " + roleName));

		user.setRole(role);
		user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
		user.setCreatedAt(LocalDateTime.now());
		return mapper.toDTO(userRepository.save(user));
	}

	@Override
	@Transactional
	public UserDTO update(Long id, UserDTO dto) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User khong ton tai"));

		if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
			throw new IllegalArgumentException("Email da ton tai");
		}
		if (!user.getUsername().equalsIgnoreCase(dto.getUsername())
				&& userRepository.existsByUsername(dto.getUsername())) {
			throw new IllegalArgumentException("Username da ton tai");
		}

		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setFullName(dto.getFullName());
		user.setEnabled(dto.isEnabled());

		if (dto.getRoleName() != null && !dto.getRoleName().isBlank()) {
			Role role = roleRepository.findByName(dto.getRoleName())
					.orElseThrow(() -> new IllegalArgumentException("Role khong ton tai: " + dto.getRoleName()));
			user.setRole(role);
		}

		return mapper.toDTO(userRepository.save(user));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User khong ton tai"));

		long productCount = userRepository.countProductsByUserId(id);
		if (productCount > 0) {
			throw new IllegalArgumentException(
					"User nay dang so huu " + productCount + " san pham, khong the xoa. "
							+ "Hay chuyen/xoa san pham cua user truoc.");
		}

		userRepository.delete(user);
	}

	@Override
	@Transactional(readOnly = true)
	public long countUsers() {
		return userRepository.count();
	}

	@Override
	@Transactional(readOnly = true)
	public long countProducts(Long userId) {
		return userRepository.countProductsByUserId(userId);
	}
}
