package com.marketingManagement.marketin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.marketingManagement.marketin",
		"config",
		"controller",
		"exception",
		"services"
})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
public class MarketinApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketinApplication.class, args);
	}

}
