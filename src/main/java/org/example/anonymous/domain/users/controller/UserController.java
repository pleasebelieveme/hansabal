package org.example.anonymous.domain.users.controller;

import org.example.anonymous.domain.users.dto.request.UserRequestDto;
import org.example.anonymous.domain.users.dto.response.UserResponseDto;
import org.example.anonymous.domain.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto reqDto) {
		return ResponseEntity.ok(userService.createUser(reqDto));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) throws Exception {
		return ResponseEntity.ok(userService.findById(id));
	}
}
