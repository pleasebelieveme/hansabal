package org.example.hansabal.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.siot.IamportRestClient.IamportClient;

@Configuration
public class IAmPortEnvConfig {

	@Value("${portone.open}")
	String apiKey;
	@Value("${portone.secret}")
	String secretKey;

	@Bean
	public IamportClient iamportClient() {
		return new IamportClient(apiKey, secretKey);
	}
}
