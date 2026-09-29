package web.programming.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import web.programming.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

	/** Tim kiem + phan trang (theo ten hoac mo ta) */
	@Query("""
			select p from Product p
			left join fetch p.user
			where lower(p.productName) like lower(concat('%', :keyword, '%'))
			   or lower(coalesce(p.description, '')) like lower(concat('%', :keyword, '%'))
			""")
	Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

	/** Kiem tra trung ten san pham */
	Optional<Product> findByProductName(String productName);

	/** Dem so san pham cua 1 nguoi dung (quan he 1 user - n product) */
	long countByUser_Id(Long userId);
}
