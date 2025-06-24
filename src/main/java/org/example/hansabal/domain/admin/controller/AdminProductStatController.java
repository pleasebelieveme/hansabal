package org.example.hansabal.domain.admin.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.request.ProductTradeStatRequest;
import org.example.hansabal.domain.admin.response.ProductTradeStatResponse;
import org.example.hansabal.domain.admin.service.AdminProductStatService;
import org.example.hansabal.domain.product.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/stats/Product")
public class AdminProductStatController {

	private final AdminProductStatService productStatService;

	@GetMapping("/Trades")
	public ResponseEntity<ProductTradeStatResponse> getTradeStat(
		@Valid @ModelAttribute ProductTradeStatRequest req
	) {
		ProductTradeStatResponse res = productStatService.getTradeStat(req);
		return ResponseEntity.ok(res);
	}
}
