package com.devteria.identity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
public class IdentityServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(IdentityServiceApplication.class, args);
    String swaggerUrl = "http://localhost:8080/swagger-ui/index.html";
    System.out.println("Swagger UI: " + swaggerUrl);
  }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
