package com.youtube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class YoutubeanalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubeanalyticsApplication.class, args);
	}

}
