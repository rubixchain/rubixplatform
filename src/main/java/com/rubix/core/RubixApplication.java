package com.rubix.core;

import java.io.IOException;
import java.text.ParseException;

import com.rubix.core.Controllers.Basics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class RubixApplication {

	public static void main(String[] args) throws ParseException, IOException {

		System.setProperty("server.port", String.valueOf(1898));
		SpringApplication.run(RubixApplication.class, args);
		Basics.start();

	}

	// //Adding CORS - Desktop Applications
	// @Bean
	// public WebMvcConfigurer corsConfigurer() {
	// return new WebMvcConfigurerAdapter() {
	// @Override
	// public void addCorsMappings(CorsRegistry registry) {
	// registry.addMapping("/**")
	// .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE",
	// "PATCH").allowedOrigins("app://.");
	// }
	// };
	// }

	// //Adding CORS - LocalHost
	// @Bean
	// public WebMvcConfigurer corsConfigurer() {
	// return new WebMvcConfigurerAdapter() {
	// @Override
	// public void addCorsMappings(CorsRegistry registry) {
	// registry.addMapping("/**")
	// .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE",
	// "PATCH").allowedOrigins("http://localhost:8081");
	// }
	// };
	// }

	// Adding CORS - All Origins
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH").allowedOrigins("*");
			}
		};
	}

}
