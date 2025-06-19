package org.example.hansabal.domain.users.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	default User findByEmailOrElseThrow(String email) {
		return findByEmail(email).orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));
	}

	default User findByIdOrElseThrow(Long id) {
		return findById(id)
				.filter(user -> !user.isDeleted())
				.orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));
	}

	boolean existsByNickname(String nickname);

	Optional<User> findByNickname(String nickname);

	List<User> findAllByLastLoginAtBeforeAndUserStatus(LocalDateTime time, UserStatus status);

}
