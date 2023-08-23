package com.trip.triptogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TripTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripTogetherApplication.class, args);
	}

}
