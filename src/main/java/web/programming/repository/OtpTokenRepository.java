package web.programming.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.programming.entity.OtpToken;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

	/** OTP chua dung, moi nhat - dung de verify */
	Optional<OtpToken> findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(String email, String type);

	/** Xoa OTP cu truoc khi gui OTP moi */
	void deleteByEmailAndType(String email, String type);

	/** OTP moi nhat (ke ca da dung) - dung de kiem tra thoi gian cho resend */
	Optional<OtpToken> findTopByEmailAndTypeOrderByCreatedAtDesc(String email, String type);
}
