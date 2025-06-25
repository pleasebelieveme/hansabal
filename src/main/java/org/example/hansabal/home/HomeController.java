package org.example.hansabal.home;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.service.TokenService;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final UserRepository userRepository;
	private final TokenService tokenService;
	// 루트 접근 시 → /home 으로 리디렉트
	@GetMapping("/")
	public String redirectRootToHome() {
		return "redirect:/home";
	}

	@GetMapping("/home")
	public String home(Model model, @AuthenticationPrincipal UserAuth userAuth) {
		if (userAuth != null) {
			model.addAttribute("username", userAuth.getNickname());
		}
		return "home"; // templates/home.html
	}
	@GetMapping("/login")
	public String loginPage() {
		return "login"; // login.html
	}

}
