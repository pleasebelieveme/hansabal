package org.example.hansabal.domain.users.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.users.dto.request.UserCreateRequestDto;
import org.example.hansabal.domain.users.dto.request.UserUpdateRequestDto;
import org.example.hansabal.domain.users.dto.response.UserResponseDto;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void createUser(@RequestBody UserCreateRequestDto request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BizException(UserErrorCode.DUPLICATE_USER_ID);
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.name(request.getName())
			.nickname(request.getNickname())
			.userRole(request.getUserRole() != null ? request.getUserRole() : UserRole.USER)
			.build();

		userRepository.save(user);
	}

	public UserResponseDto findById(UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		return UserResponseDto.toDto(findUser);
	}

	@Transactional
	public void updateUser(UserUpdateRequestDto request, UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		checkPassword(request.getOldPassword(), findUser.getPassword());

		if (request.getNickname() == null && request.getNewPassword() == null) {
			throw new BizException(UserErrorCode.NO_UPDATE_TARGET);
		}
		if (request.getNickname() != null && findUser.getNickname().equals(request.getNickname())) {
			throw new BizException(UserErrorCode.NICKNAME_NOT_CHANGED);
		}
		if (request.getNewPassword() != null && passwordEncoder.matches(request.getNewPassword(), findUser.getPassword())) {
			throw new BizException(UserErrorCode.PASSWORD_NOT_CHANGED);
		}

		String encodedPassword = passwordEncoder.encode(request.getNewPassword());
		findUser.updateUser(request.getNickname(), encodedPassword);
	}

	private void checkPassword(String rawPassword, String hashedPassword) {
		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}
	}
}
