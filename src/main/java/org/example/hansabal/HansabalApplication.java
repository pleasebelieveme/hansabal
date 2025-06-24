package org.example.hansabal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HansabalApplication {

	public static void main(String[] args) {
		SpringApplication.run(HansabalApplication.class, args);
	}
}
