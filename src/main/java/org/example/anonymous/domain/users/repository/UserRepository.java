package org.example.anonymous.domain.users.repository;

import java.util.Optional;

import org.example.anonymous.domain.users.entity.User;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	default User findByEmailOrElseThrow(String email) throws Exception {
		return findByEmail(email).orElseThrow(() -> new Exception("해당하는 이메일이 없습니다. :: " + email));
	}
}
