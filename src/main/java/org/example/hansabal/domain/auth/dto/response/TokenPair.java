package org.example.hansabal.domain.auth.dto.response;


public record TokenPair (
	String accessToken,
	String refreshToken
){
}
