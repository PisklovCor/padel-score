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
@Schema(description = "Запись в таблице лидеров команды")
public class LeaderboardEntryDto {

  @Schema(description = "Идентификатор команды", example = "1")
  private Integer teamId;

  @Schema(description = "Название команды", example = "Команда Чемпионов", maxLength = 255)
  private String teamName;

  @Schema(description = "Количество сыгранных матчей", example = "10")
  private Integer matches;

  @Schema(description = "Количество побед", example = "7")
  private Integer wins;

  @Schema(description = "Количество поражений", example = "3")
  private Integer losses;

  @Schema(description = "Количество выигранных сетов", example = "15")
  private Integer setsWon;

  @Schema(description = "Количество проигранных сетов", example = "8")
  private Integer setsLost;

  @Schema(description = "Количество выигранных геймов", example = "120")
  private Integer gamesWon;

  @Schema(description = "Количество проигранных геймов", example = "95")
  private Integer gamesLost;

  @Schema(description = "Накопленные очки", example = "21")
  private Integer points;

  @Schema(description = "Процент побед (от 0.0 до 1.0)", example = "0.7")
  private Double winRate;

}
