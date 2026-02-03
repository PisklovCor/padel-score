package com.padelscore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Validated
@ConfigurationProperties(prefix = "application.properties")
public class PropertiesConfiguration {

  @NotBlank
  private String botToken;

  @NotBlank
  private String botUsername;

  @NotBlank
  private String botAdminId;

}
