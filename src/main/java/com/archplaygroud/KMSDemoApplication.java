package com.archplaygroud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KMSDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KMSDemoApplication.class, args);
    }

}
