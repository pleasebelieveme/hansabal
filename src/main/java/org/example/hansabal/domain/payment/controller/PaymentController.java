package org.example.hansabal.domain.payment.controller;

import org.example.hansabal.domain.payment.dto.request.RequestPay;
import org.example.hansabal.domain.payment.dto.request.PaymentCallbackRequest;
import org.example.hansabal.domain.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping("/api/payment/{uuid}")
	public String paymentPage(@PathVariable(name = "uuid", required = false) String uuid,
		Model model) {

		RequestPay requestDto = paymentService.findRequestDto(uuid);
		model.addAttribute("requestDto", requestDto);
		return "payment";
	}

	@ResponseBody
	@PostMapping("/api/payment")
	public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
		IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

		log.info("결제 응답={}", iamportResponse.getResponse().toString());

		return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
	}

	@GetMapping("/api/success-payment")
	public String successPaymentPage() {
		return "success-payment";
	}

	@GetMapping("/api/fail-payment")
	public String failPaymentPage() {
		return "fail-payment";
	}
}
