package web.programming.service;

import org.springframework.data.domain.Page;

import web.programming.dto.UserDTO;

/**
 * Nghiep vu quan ly nguoi dung: CRUD + tim kiem + phan trang + thong ke.
 */
public interface UserService {

	Page<UserDTO> findAll(String keyword, int page, int size);

	UserDTO findById(Long id);

	UserDTO create(UserDTO dto);

	UserDTO update(Long id, UserDTO dto);

	void delete(Long id);

	long countUsers();

	long countProducts(Long userId);
}
