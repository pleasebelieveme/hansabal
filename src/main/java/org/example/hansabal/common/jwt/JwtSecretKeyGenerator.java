package org.example.hansabal.common.jwt;

import java.util.Base64;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtSecretKeyGenerator {
	public static void main(String[] args) {
		byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
		String base64Key = Base64.getEncoder().encodeToString(key);
		System.out.println("Generated Secret Key: " + base64Key);
	}
}
