package com.workshop.pizza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PizzaShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PizzaShopApplication.class, args);
	}

}
