package com.rubix.core;

import com.rubix.core.Controllers.Basics;
import org.json.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.rubix.Resources.Functions.buildVersion;
import static com.rubix.Resources.Functions.initHash;

@SpringBootApplication
public class RubixApplication {


	public static void main(String[] args) throws ParseException, IOException, JSONException {

		System.setProperty("server.port", String.valueOf(1898));
		SpringApplication.run(RubixApplication.class, args);
		System.out.println("Build Version: " + buildVersion());
		System.out.println("Build Version 2: " + initHash());
		System.out.println("Jar started: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		Basics.start();

	}

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
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH").allowedOrigins("*");
			}
		};
	}


}
