package com.padelscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.padelscore.config.PropertiesConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfiguration.class)
public class PadelScoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(PadelScoreApplication.class, args);
  }
}
