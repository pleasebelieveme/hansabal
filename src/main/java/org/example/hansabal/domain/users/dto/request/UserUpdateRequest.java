package org.example.hansabal.domain.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UserUpdateRequest(

	@Schema(description = "변경할 닉네임 (최대 30자)", example = "새로운닉네임")
	@Size(max = 30, message = "이름 최대 30글자가 넘지 않도록 해주십시오.")
	String nickname,

	@Schema(description = "현재 비밀번호", example = "CurrentPass123!")
	@NotBlank(message = "현재 비밀번호를 입력해주세요.")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$",
			message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다"
	)
	String oldPassword,

	@Schema(description = "변경할 새 비밀번호", example = "NewPass456!")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,}$",
			message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다"
	)
	String newPassword

) { }
