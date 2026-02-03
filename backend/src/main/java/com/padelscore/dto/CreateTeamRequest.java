package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание команды")
public class CreateTeamRequest {

  @NotNull(message = "ID турнира обязателен")
  @Schema(description = "Идентификатор турнира", example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer tournamentId;

  @NotBlank(message = "Название команды обязательно")
  @Size(max = 255)
  @Schema(description = "Название команды", example = "Команда Чемпионов",
      requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
  private String name;

  @NotNull(message = "ID профиля капитана обязателен")
  @Schema(description = "Идентификатор профиля игрока-капитана", example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer captainPlayerProfileId;

  @Size(max = 1000)
  @Schema(description = "Описание команды", example = "Сильная команда с опытом игры",
      maxLength = 1000)
  private String description;

  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Цвет должен быть в формате HEX (#RRGGBB)")
  @Schema(description = "Цвет команды в формате HEX (#RRGGBB)", example = "#FF5733",
      pattern = "^#[0-9A-Fa-f]{6}$")
  private String color;
}
