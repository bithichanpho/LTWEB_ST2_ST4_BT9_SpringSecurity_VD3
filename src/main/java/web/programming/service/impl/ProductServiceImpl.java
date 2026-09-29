package web.programming.service.impl;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import web.programming.dto.ProductDTO;
import web.programming.entity.Product;
import web.programming.entity.User;
import web.programming.mapper.ProductMapper;
import web.programming.repository.ProductRepository;
import web.programming.repository.UserRepository;
import web.programming.service.CloudinaryService;
import web.programming.service.CloudinaryUploadResult;
import web.programming.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private static final Pattern ID_PATTERN = Pattern.compile("^SP(\\d+)$");

	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ProductMapper mapper;
	private final CloudinaryService cloudinaryService;

	@Override
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
				Sort.by(Sort.Direction.DESC, "productId"));
		return productRepository.search(keyword == null ? "" : keyword, pageable).map(mapper::toDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDTO findById(String id) {
		return mapper.toDTO(productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Product khong ton tai")));
	}

	@Override
	@Transactional
	public ProductDTO create(ProductDTO dto, MultipartFile image) {
		if (productRepository.findByProductName(dto.getName()).isPresent()) {
			throw new IllegalArgumentException("Ten san pham '" + dto.getName() + "' da ton tai");
		}

		User owner = null;
		if (dto.getUserId() != null) {
			owner = userRepository.findById(dto.getUserId())
					.orElseThrow(() -> new IllegalArgumentException("User khong ton tai"));
		}

		Product product = new Product();
		product.setProductId(generateNextProductId());
		product.setProductName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setCreatedAt(LocalDateTime.now());
		product.setUser(owner);

		if (image != null && !image.isEmpty()) {
			product.setImages(storeImage(image));
		}
		return mapper.toDTO(productRepository.save(product));
	}

	@Override
	@Transactional
	public ProductDTO update(String id, ProductDTO dto, MultipartFile image) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Product khong ton tai"));

		if (!product.getProductName().equalsIgnoreCase(dto.getName())
				&& productRepository.findByProductName(dto.getName()).isPresent()) {
			throw new IllegalArgumentException("Ten san pham '" + dto.getName() + "' da ton tai");
		}

		product.setProductName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());

		if (image != null && !image.isEmpty()) {
			deleteStoredImage(product.getImages());
			product.setImages(storeImage(image));
		}
		return mapper.toDTO(productRepository.save(product));
	}

	@Override
	@Transactional
	public void delete(String id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Product khong ton tai"));
		deleteStoredImage(product.getImages());
		productRepository.delete(product);
	}

	@Override
	@Transactional(readOnly = true)
	public long countProducts() {
		return productRepository.count();
	}

	@Override
	@Transactional(readOnly = true)
	public long countByUser(Long userId) {
		return productRepository.countByUser_Id(userId);
	}

	// ==================== Helper ====================

	/** Sinh ma san pham tiep theo: SP01, SP02, ... */
	private String generateNextProductId() {
		int max = 0;
		for (Product p : productRepository.findAll()) {
			Matcher m = ID_PATTERN.matcher(p.getProductId());
			if (m.matches()) {
				max = Math.max(max, Integer.parseInt(m.group(1)));
			}
		}
		return String.format("SP%02d", max + 1);
	}

	/** Upload anh (Cloudinary hoac local fallback) -> chuoi luu vao cot images */
	private String storeImage(MultipartFile image) {
		CloudinaryUploadResult result = cloudinaryService.upload(image);
		return result.publicId() == null ? result.url() : result.url() + "|" + result.publicId();
	}

	/** Xoa anh theo chuoi da luu ("url|publicId") */
	private void deleteStoredImage(String stored) {
		if (stored == null || stored.isBlank() || !stored.contains("|")) {
			return;
		}
		String publicId = stored.substring(stored.indexOf('|') + 1);
		cloudinaryService.delete(publicId);
	}
}
