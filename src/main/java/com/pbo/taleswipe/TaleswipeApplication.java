package com.PBO.TaleSwipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.PBO.TaleSwipe.config.security.JwtProperties;

@SpringBootApplication(scanBasePackages = "com.PBO.TaleSwipe")
@EntityScan(basePackages = "com.PBO.TaleSwipe.model") // ✅ agar Hibernate tahu lokasi @Entity
@EnableConfigurationProperties(JwtProperties.class)
public class TaleswipeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaleswipeApplication.class, args);
    }
}
