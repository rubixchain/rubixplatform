package com.rubix.core;

import com.rubix.core.Controllers.Basics;
import static com.rubix.core.Resources.Version.*;
import static com.rubix.core.Resources.CallerFunctions.*;
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
import static com.rubix.Resources.Functions.setBasicWalletType;
import static com.rubix.Resources.Functions.tokenStringGen;
import static com.rubix.Resources.Functions.IdentityToken;

@SpringBootApplication
public class RubixApplication {

	public static void main(String[] args) throws ParseException, IOException {

		System.setProperty("server.port", String.valueOf(1898));
		SpringApplication.run(RubixApplication.class, args);
		getVersion();
		System.out.println("Release Version : " + jarVersion);
		System.out.println("Build Version: " + buildVersion());
		System.out.println("Jar started on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " UTC");

		if (mainDir()) {
			setBasicWalletType();
		}

		Basics.start();

		if (mainDir()) {
			System.out.println("Please save the token. Valid till Node session ends i.e. node service shutsdown");
			System.out.println("<################################>");
			tokenStringGen();
			System.out.println("AuthToken : " + IdentityToken);
			System.out.println("<################################>");
		}
	}

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
