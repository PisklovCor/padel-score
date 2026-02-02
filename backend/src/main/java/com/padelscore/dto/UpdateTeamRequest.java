package com.padelscore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamRequest {

  @NotBlank(message = "Название команды обязательно")
  private String name;

  private Integer captainPlayerProfileId;

  private String description;

  private String color;
}
