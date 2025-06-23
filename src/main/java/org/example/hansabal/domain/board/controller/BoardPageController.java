package org.example.hansabal.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.service.BoardService;
import org.example.hansabal.domain.board.service.BoardServiceUtill;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//<from> 동작을 위한 controller
@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardPageController {
    private final BoardService boardService;
    private final BoardServiceUtill boardServiceUtill;

    @GetMapping("/community")
    public String communityList(
            @RequestParam(name = "category", defaultValue = "ALL") BoardCategory category,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        // 페이지 번호는 0부터 시작하므로 -1 해줌
        Page<BoardSimpleResponse> postsPage = boardService.getPosts(category, keyword, page -1, size);

        model.addAttribute("posts", postsPage);  // getContent() 말고 전체 Page 객체를 넘김
        model.addAttribute("categories", BoardCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postsPage.getTotalPages());

        return "community"; // community.html 뷰 렌더링
    }

    @GetMapping("/community/{postId}")
    public String getPostDetail(@PathVariable("postId") Long postId, Model model) {
        boardServiceUtill.viewCount(postId);
        BoardResponse post = boardService.getPost(postId);
        model.addAttribute("post", post);
        return "post";
    }

    // 게시글 작성 페이지 보여주기
    @GetMapping("/write")
    public String showWriteForm(Model model) {
        model.addAttribute("boardRequest", new BoardRequest());
        return "write";  // src/main/resources/templates/write.html
    }

    @PostMapping("/write")
    public String writePost(@Valid BoardRequest boardRequest,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserAuth userAuth,
                            RedirectAttributes redirectAttributes) {
        System.out.println("✅ [writePost 호출됨]");
        log.info("📨 writePost 호출됨");

        if (userAuth == null) {
            log.warn("⛔ 로그인 정보 없음. 리다이렉트");
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            log.warn("⚠️ 유효성 검증 실패: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.boardRequest", bindingResult);
            redirectAttributes.addFlashAttribute("boardRequest", boardRequest);
            return "redirect:/write";
        }

        log.info("🟢 검증 성공 → 글 저장 로직 진입");
        boardService.createBoard(userAuth, boardRequest);
        return "redirect:/community";
    }


}
