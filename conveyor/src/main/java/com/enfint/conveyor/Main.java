package com.enfint.conveyor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;


@SpringBootApplication
public class Main {

    @Autowired
    public RestTemplate restTemplate(){
        return new RestTemplate();

    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
