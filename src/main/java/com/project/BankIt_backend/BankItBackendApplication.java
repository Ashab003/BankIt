package com.project.BankIt_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableCaching //for redis
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BankItBackendApplication {

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.version"));
		SpringApplication.run(BankItBackendApplication.class, args);
	}


}
