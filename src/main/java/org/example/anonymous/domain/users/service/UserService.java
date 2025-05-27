package org.example.anonymous.domain.users.service;

import java.util.List;

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
		User savedUser = userRepository.save(new User(reqDto.getEmail(), reqDto.getPassword(), reqDto.getName()));
		return new UserResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getName());
	}

	public UserResponseDto findById(Long id) throws Exception {
		User findUser = userRepository.findByIdOrElseThrow(id);
		return new UserResponseDto(findUser.getId(), findUser.getEmail(), findUser.getName());
	}
}
