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
@Schema(description = "Результат матча")
public class MatchResultDto {

  @Schema(description = "Идентификатор матча", example = "1")
  private Integer matchId;

  @Schema(description = "Идентификатор команды-победителя", example = "1")
  private Integer winnerTeamId;

  @Schema(description = "Идентификатор команды-проигравшего", example = "2")
  private Integer loserTeamId;

  @Schema(description = "Название команды-победителя", example = "Команда А", maxLength = 255)
  private String winnerTeamName;

  @Schema(description = "Название команды-проигравшего", example = "Команда Б", maxLength = 255)
  private String loserTeamName;

  @Schema(description = "Финальный счет матча (формат: 2-0 или 2-1)", example = "2-1", maxLength = 50)
  private String finalScore;

  @Schema(description = "Очки команды-победителя", example = "21")
  private Integer winnerPoints;

  @Schema(description = "Очки команды-проигравшего", example = "15")
  private Integer loserPoints;

  @Schema(description = "Результат оспорен", example = "false")
  private Boolean disputed;
}
