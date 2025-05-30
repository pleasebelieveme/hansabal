package org.example.hansabal.domain.comment.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.comment.dto.request.DibRequest;
import org.example.hansabal.domain.comment.service.DibService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dibs")
@RequiredArgsConstructor
public class DibController {

	private final DibService dibService;

	@PostMapping
	public ResponseEntity<Void> modifyDibs(
		@AuthenticationPrincipal UserAuth userAuth,
		@Valid @RequestBody DibRequest request){

		dibService.modifyDibs(userAuth.getId(),request);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
