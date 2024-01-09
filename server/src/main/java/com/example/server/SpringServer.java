package com.example.server;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringServer {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringServer.class, args);
        System.out.println("Started Server");
    }
}
