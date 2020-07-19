package com.rjoydip.sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${sftp.PORT}")
    private int PORT;
    @Value("${sftp.HOST}")
    private String HOST;
    @Value("${sftp.REMOTE.USERNAME}")
    private String REMOTE_USERNAME;
    @Value("${sftp.REMOTE.PASSWORD}")
	private String REMOTE_PASSWORD;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			logger.info("Application working");
			logger.info("PORT {}", PORT);
			logger.info("HOST {}", HOST);
			logger.info("REMOTE.USERNAME {}", REMOTE_USERNAME);
			logger.info("REMOTE.PASSWORD {}", REMOTE_PASSWORD);
		};
	}
}
