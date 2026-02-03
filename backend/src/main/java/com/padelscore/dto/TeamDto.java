package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о команде")
public class TeamDto {

  @Schema(description = "Идентификатор команды", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Integer id;

  @Schema(description = "Идентификатор турнира", example = "1")
  private Integer tournamentId;

  @Schema(description = "Название команды", example = "Команда Чемпионов", maxLength = 255)
  private String name;

  @Schema(description = "Идентификатор профиля игрока-капитана", example = "1")
  private Integer captainPlayerProfileId;

  @Schema(description = "Описание команды", example = "Сильная команда с опытом игры", maxLength = 1000)
  private String description;

  @Schema(description = "Цвет команды в формате HEX (#RRGGBB)", example = "#FF5733", maxLength = 7)
  private String color;
}
