package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о матче")
public class MatchDto {

  @Schema(description = "Идентификатор матча", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Integer id;

  @Schema(description = "Идентификатор турнира", example = "1")
  private Integer tournamentId;

  @Schema(description = "Идентификатор первой команды", example = "1")
  private Integer team1Id;

  @Schema(description = "Идентификатор второй команды", example = "2")
  private Integer team2Id;

  @Schema(description = "Название первой команды", example = "Команда А", maxLength = 255)
  private String team1Name;

  @Schema(description = "Название второй команды", example = "Команда Б", maxLength = 255)
  private String team2Name;

  @Schema(description = "Запланированная дата и время матча", example = "2024-02-15T18:00:00")
  private LocalDateTime scheduledDate;

  @Schema(description = "Статус матча: SCHEDULED — запланирован, IN_PROGRESS — в процессе, "
      + "COMPLETED — завершен, CANCELLED — отменен",
      example = "SCHEDULED", allowableValues = {"SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"})
  private String status;

  @Schema(description = "Формат матча: best_of_3_sets — до 2 побед, best_of_5_sets — до 3 побед",
      example = "best_of_3_sets", allowableValues = {"best_of_3_sets", "best_of_5_sets"})
  private String format;

  @Schema(description = "Место проведения матча", example = "Корт №1", maxLength = 255)
  private String location;

  @Schema(description = "Флаг завершенности матча", example = "false")
  private Boolean completed;
}
