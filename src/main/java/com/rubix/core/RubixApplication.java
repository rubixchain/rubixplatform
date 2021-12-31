package com.rubix.core;

import java.io.IOException;
import java.text.ParseException;

import com.rubix.LevelDb.DataBase;
import com.rubix.core.Controllers.Basics;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@SpringBootApplication
public class RubixApplication {


	public static void main(String[] args) throws IOException {


		System.setProperty("server.port", String.valueOf(1898));
		SpringApplication.run(com.rubix.core.RubixApplication.class, args);
		//Basics.sync();
		DataBase.createOrOpenDB();
		//DataBase.pushTxnFiletoDB();


	}

	/* public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			context.close();
			context = SpringApplication.run(RubixApplication.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	} */

//	//Adding CORS - Desktop Applications
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurerAdapter() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**")
//						.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH").allowedOrigins("app://.");
//			}
//		};
//	}

//	//Adding CORS - LocalHost
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurerAdapter() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**")
//						.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH").allowedOrigins("http://localhost:8081");
//			}
//		};
//	}

	//Adding CORS - All Origins
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
