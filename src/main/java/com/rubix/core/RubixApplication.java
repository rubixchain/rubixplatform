package com.rubix.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class RubixApplication {

	public static void main(String[] args) {
		System.setProperty("server.port", String.valueOf(1898));
		SpringApplication.run(RubixApplication.class, args);

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
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH").allowedOrigins("*");
			}
		};
	}

}
