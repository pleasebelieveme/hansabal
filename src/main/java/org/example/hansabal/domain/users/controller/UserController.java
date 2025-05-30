package org.example.hansabal.domain.users.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.users.dto.request.UserRequestDto;
import org.example.hansabal.domain.users.dto.request.UserUpdateRequestDto;
import org.example.hansabal.domain.users.dto.response.UserResponseDto;
import org.example.hansabal.domain.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<Void> createUser(@RequestBody UserRequestDto request) {
		userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDto> findById(UserAuth userAuth) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.findById(userAuth));
	}

	@PatchMapping
	public ResponseEntity<Void> updateUser(@Valid @RequestBody UserUpdateRequestDto request, UserAuth userAuth) {
		userService.updateUser(request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
