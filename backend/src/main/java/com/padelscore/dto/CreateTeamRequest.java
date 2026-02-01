package com.padelscore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamRequest {

  @NotNull(message = "ID турнира обязателен")
  private Integer tournamentId;

  @NotBlank(message = "Название команды обязательно")
  private String name;

  @NotNull(message = "ID капитана обязателен")
  private Long captainId;

  private String description;

  private String color; // HEX color
}
