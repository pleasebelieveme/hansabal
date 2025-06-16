package org.example.hansabal.domain.users.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.dto.request.UserUpdateRequest;
import org.example.hansabal.domain.users.dto.response.UserResponse;
import org.example.hansabal.domain.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "UserController", description = "회원(User) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@Operation(
		summary = "회원가입",
		description = "신규 사용자를 생성합니다. 이메일, 비밀번호, 이름, 닉네임 등을 입력받습니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "400", description = "요청 형식 오류")
	})
	@PostMapping
	public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateRequest request) {
		userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(
		summary = "내 정보 조회",
		description = "현재 로그인한 사용자의 정보를 조회합니다."
	)
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping("/me")
	public ResponseEntity<UserResponse> findById(@AuthenticationPrincipal UserAuth userAuth) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.findById(userAuth));
	}

	@Operation(
		summary = "회원 정보 수정",
		description = "현재 로그인한 사용자의 정보를 수정합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PatchMapping
	public ResponseEntity<Void> updateUser(
		@Valid @RequestBody UserUpdateRequest request,
		@AuthenticationPrincipal UserAuth userAuth
	) {
		userService.updateUser(request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(
		summary = "회원 탈퇴",
		description = "현재 로그인한 사용자를 탈퇴 처리합니다."
	)
	@ApiResponse(responseCode = "200", description = "탈퇴 성공")
	@DeleteMapping
	public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserAuth userAuth) {
		userService.deleteUser(userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
