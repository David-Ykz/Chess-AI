package com.example.server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringServer {
    public static void main(String[] args) {
        SpringApplication.run(SpringServer.class, args);
        System.out.println("fish");
    }
}
