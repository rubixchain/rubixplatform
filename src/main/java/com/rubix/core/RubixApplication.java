package com.rubix.core;

import static com.rubix.Resources.Functions.buildVersion;
import static com.rubix.Resources.Functions.dirPath;
import static com.rubix.core.Resources.Version.getVersion;
import static com.rubix.core.Resources.Version.jarVersion;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.rubix.Datum.Dependency;
import com.rubix.core.Controllers.Basics;
import com.rubix.core.NFTController.NftOperations;

@SpringBootApplication
public class RubixApplication {

	public static void main(String[] args) throws ParseException, IOException, JSONException {

		System.setProperty("server.port", String.valueOf(1898));
		SpringApplication.run(RubixApplication.class, args);
		getVersion();
		System.out.println("Release Version : " + jarVersion);
		System.out.println("Build Version: " + buildVersion());
		System.out.println("Jar started on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " UTC");
		Basics.start();
		NftOperations.enableNFT();
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
