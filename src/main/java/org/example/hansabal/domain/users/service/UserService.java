package org.example.hansabal.domain.users.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.auth.util.PasswordEncoder;
import org.example.hansabal.domain.users.dto.request.UserRequestDto;
import org.example.hansabal.domain.users.dto.response.UserResponseDto;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void createUser(@RequestBody UserRequestDto request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BizException(UserErrorCode.DUPLICATE_USER_ID);
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.name(request.getName())
			.nickname(request.getNickname())
			.userRole(request.getUserRole())
			.build();

		userRepository.save(user);
	}

	public UserResponseDto findById(Long id) {
		User findUser = userRepository.findByIdOrElseThrow(id);
		return UserResponseDto.toDto(findUser);
	}
}
