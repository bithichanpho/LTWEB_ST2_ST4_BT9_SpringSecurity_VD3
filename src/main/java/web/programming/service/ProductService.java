package web.programming.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import web.programming.dto.ProductDTO;

/**
 * Nghiep vu san pham: CRUD + tim kiem + phan trang + upload anh (Cloudinary).
 */
public interface ProductService {

	Page<ProductDTO> findAll(String keyword, int page, int size);

	ProductDTO findById(String id);

	ProductDTO create(ProductDTO dto, MultipartFile image);

	ProductDTO update(String id, ProductDTO dto, MultipartFile image);

	void delete(String id);

	long countProducts();

	long countByUser(Long userId);
}
