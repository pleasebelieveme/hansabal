package org.example.anonymous.domain.users.service;

import org.example.anonymous.domain.users.dto.request.UserRequestDto;
import org.example.anonymous.domain.users.dto.response.UserResponseDto;
import org.example.anonymous.domain.users.entity.User;
import org.example.anonymous.domain.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public UserResponseDto createUser(@RequestBody UserRequestDto reqDto) {
		User user = new User(reqDto.getEmail(), reqDto.getPassword(), reqDto.getName());
		User savedUser = userRepository.save(user);
		return new UserResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getName());
	}
}
