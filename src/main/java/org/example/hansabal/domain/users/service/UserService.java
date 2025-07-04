package org.example.hansabal.domain.users.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.email.exception.EmailErrorCode;
import org.example.hansabal.domain.email.service.MailService;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.dto.request.UserUpdateRequest;
import org.example.hansabal.domain.users.dto.response.UserResponse;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.wallet.repository.WalletRepository;
import org.example.hansabal.domain.wallet.service.WalletService;
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
	private final MailService mailService;
	private final RedisRepository redisRepository;
	private final WalletService walletService;
	private final WalletRepository walletRepository;

	public void createUser(@RequestBody UserCreateRequest request) {
		// // 1) 이메일 인증 완료 여부 확인
		//  boolean isVerified = redisRepository.hasKey("EMAIL_VERIFIED:" + request.email());
		//  if (!isVerified) {
		//  	throw new BizException(EmailErrorCode.EMAIL_NOT_VERIFIED);
		//  }

		 // 2) 이메일 중복 검사
		 if (userRepository.existsByEmail(request.email())) {
		 	throw new BizException(UserErrorCode.DUPLICATE_USER_EMAIL);
		 }

		// 3) 비밀번호 암호화 및 회원 생성
		String encodedPassword = passwordEncoder.encode(request.password());
		User user = User.builder()
				.email(request.email())
				.password(encodedPassword)
				.name(request.name())
				.nickname(request.nickname())
				.userRole(request.userRole())
				.build();

		userRepository.save(user);
		walletService.createWallet(user);
		 // mailService.signUpCompletedEmail(request.name(), request.email());

		// // 4) 가입 성공 시 Redis에서 인증 완료 플래그 삭제
		//  redisRepository.delete("EMAIL_VERIFIED:" + request.email());
	}


	public UserResponse findById(UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		return UserResponse.from(findUser);
	}

	@Transactional
	public void updateUser(UserUpdateRequest request, UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		checkPassword(request.oldPassword(), findUser.getPassword());

		if (request.nickname() == null && request.newPassword() == null) {
			throw new BizException(UserErrorCode.NO_UPDATE_TARGET);
		}
		if (request.nickname() != null && findUser.getNickname().equals(request.nickname())) {
			throw new BizException(UserErrorCode.NICKNAME_NOT_CHANGED);
		}
		if (request.newPassword() != null && passwordEncoder.matches(request.newPassword(), findUser.getPassword())) {
			throw new BizException(UserErrorCode.PASSWORD_NOT_CHANGED);
		}

		String encodedPassword = null;
		if (request.newPassword() != null) {
			encodedPassword = passwordEncoder.encode(request.newPassword());
		}

		findUser.updateUser(request.nickname(), encodedPassword);
	}

	private void checkPassword(String rawPassword, String hashedPassword) {
		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}
	}

	@Transactional
	public void deleteUser(UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		user.softDelete();

		// 추후 유저관련 내용 삭제 로직 추가
		if (user.getWallet() != null) {
			walletRepository.delete(user.getWallet());
		}
	}
}
