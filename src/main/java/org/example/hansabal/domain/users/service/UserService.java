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

	public void createUser(@RequestBody UserRequestDto reqDto) {

		if (userRepository.existsByEmail(reqDto.getEmail())) {
			throw new BizException(UserErrorCode.DUPLICATE_USER_ID);
		}

		String encodedPassword = passwordEncoder.encode(reqDto.getPassword());

		User user = User.builder()
			.email(reqDto.getEmail())
			.password(encodedPassword)
			.name(reqDto.getName())
			.nickname(reqDto.getNickname())
			.userRole(reqDto.getUserRole())
			.build();

		userRepository.save(user);
	}

	public UserResponseDto findById(Long id) {
		User findUser = userRepository.findByIdOrElseThrow(id);
		return new UserResponseDto(findUser.getId(), findUser.getEmail(), findUser.getName(), findUser.getNickname());
	}
}
