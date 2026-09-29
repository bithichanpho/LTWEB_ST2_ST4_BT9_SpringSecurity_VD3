package web.programming.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import web.programming.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByUsernameOrEmail(String username, String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	@Query("""
			select u from User u
			where lower(u.username) like lower(concat('%', :keyword, '%'))
			   or lower(u.email) like lower(concat('%', :keyword, '%'))
			   or lower(coalesce(u.fullName, '')) like lower(concat('%', :keyword, '%'))
			""")
	Page<User> search(@Param("keyword") String keyword, Pageable pageable);

	@Query("select count(p) from Product p where p.user.id = :userId")
	long countProductsByUserId(@Param("userId") Long userId);
}
