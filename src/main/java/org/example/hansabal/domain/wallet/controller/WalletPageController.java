package org.example.hansabal.domain.wallet.controller;

import lombok.RequiredArgsConstructor;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.wallet.dto.request.LoadRequest;
import org.example.hansabal.domain.wallet.service.WalletHistoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WalletPageController {

    private final WalletHistoryService walletHistoryService;

    @GetMapping("/wallet")
    public String walletPage() {
        return "wallet";
        // templates/wallet.html을 반환
    }

    @GetMapping("/payment")
    public String paymentPage(@RequestParam("uuid") String uuid,
                              @RequestParam("cash") Long cash,
                              Model model,
                              @AuthenticationPrincipal UserAuth userAuth) {

        // getLoadRequestDto() 메서드를 사용하여 LoadRequest 객체를 생성
        LoadRequest requestDto = walletHistoryService.getLoadRequestDto(uuid, userAuth, cash);  // uuid와 userAuth로 LoadRequestDto 생성
        // cash 값을 받아서 requestDto에 설정된 값을 사용
        // requestDto는 사용자 지갑 정보와 결제 정보를 담고 있음
        // requestDto = new LoadRequest(requestDto.id(), cash);  // 추가적인 값 설정을 위한 처리 (필요시)

        // requestDto를 모델에 추가하여 뷰로 전달
        model.addAttribute("requestDto", requestDto);

        // payment.html 뷰 렌더링
        return "payment";
    }

}
