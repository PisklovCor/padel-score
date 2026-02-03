package com.padelscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PadelScoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(PadelScoreApplication.class, args);
  }
}
