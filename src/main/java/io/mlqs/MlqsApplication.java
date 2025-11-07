package io.mlqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MlqsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MlqsApplication.class, args);
    }

}
