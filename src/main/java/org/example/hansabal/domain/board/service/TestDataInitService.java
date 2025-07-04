package org.example.hansabal.domain.board.service;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestDataInitService {

	@Autowired
	private UserService userService;
	@Autowired
	private BoardService boardService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insertUsersAndBoard() {
		for (int i = 0; i < 10; i++) {
			String email = "user" + i + "@exmaple.com";
			String nickname = "nickname" + i;

			UserCreateRequest request = new UserCreateRequest(
				email,
				"@Aa123456",
				"테스트이름",
				nickname,
				UserRole.USER
			);
			userService.createUser(request);
		}

		UserAuth userAuth = new UserAuth(1L, UserRole.USER, "nickname0");
		BoardRequest boardRequest = new BoardRequest(
			BoardCategory.DAILY,
			"테스트 제목",
			"테스트 내용"
		);
		boardService.createBoard(userAuth, boardRequest);
	}
}
