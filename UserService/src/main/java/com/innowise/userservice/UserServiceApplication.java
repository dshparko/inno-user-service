package com.innowise.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @ClassName UserServiceApplication
 * @Description Entry point for the User-Service application. Initializes Spring Boot context,
 * scans configuration properties, and starts the service responsible for user-related operations.
 * @Author dshparko
 * @Date 08.09.2025 17:02
 * @Version 1.0
 */

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
