package org.example.hansabal.domain.admin.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.request.ProductOrderStatRequest;
import org.example.hansabal.domain.admin.response.ProductOrderStatResponse;
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

	@GetMapping("/orders")
	public ResponseEntity<ProductOrderStatResponse> getOrderStat(
		@Valid @ModelAttribute ProductOrderStatRequest req
	) {
		ProductOrderStatResponse res = productStatService.getOrderStat(req);
		return ResponseEntity.ok(res);
	}
}
