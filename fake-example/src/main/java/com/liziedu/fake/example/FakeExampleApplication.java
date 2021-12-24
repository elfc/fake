package com.liziedu.fake.example;

import liziedu.fake.spring.glue.EnableFakeClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFakeClients
@SpringBootApplication
public class FakeExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakeExampleApplication.class, args);
    }
}
