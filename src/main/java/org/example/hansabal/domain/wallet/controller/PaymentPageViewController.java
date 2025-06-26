package org.example.hansabal.domain.wallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/ui")
public class PaymentPageViewController {
	@GetMapping("/wallet")
	public String walletPage() {
		return "wallet";  // templates/wallet.html
	}

	@GetMapping("/payment")
	public String paymentPage(@RequestParam String uuid, @RequestParam int cash, Model model) {
		model.addAttribute("uuid", uuid);
		model.addAttribute("cash", cash);
		return "payment";  // templates/payment.html
	}
}
