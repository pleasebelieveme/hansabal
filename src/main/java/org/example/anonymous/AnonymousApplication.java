package org.example.anonymous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AnonymousApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnonymousApplication.class, args);
	}

}
