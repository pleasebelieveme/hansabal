package org.example.hansabal.domain.users.dto.request;

import org.example.hansabal.domain.users.entity.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UserCreateRequest(

	@Schema(description = "사용자 이메일", example = "user@example.com")
	@NotBlank(message = "이메일은 필수 입력값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	String email,

	@Schema(description = "비밀번호 (대소문자, 숫자, 특수문자 포함 8자 이상)", example = "Password123!")
	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$",
			message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다"
	)
	String password,

	@Schema(description = "사용자 이름", example = "홍길동")
	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Size(min = 2, max = 20, message = "이름 최대 20글자가 넘지 않도록 해주십시오.")
	String name,

	@Schema(description = "사용자 닉네임", example = "길동이")
	@NotBlank(message = "닉네임은 필수 입력값입니다.")
	@Size(min = 2, max = 30, message = "이름 최대 30글자가 넘지 않도록 해주십시오.")
	String nickname,

	@Schema(description = "사용자 권한", example = "USER")
	@NotNull
	UserRole userRole

) { }
