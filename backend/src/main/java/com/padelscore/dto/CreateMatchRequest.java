package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание матча")
public class CreateMatchRequest {

  @NotNull(message = "ID турнира обязателен")
  @Schema(description = "Идентификатор турнира", example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer tournamentId;

  @NotNull(message = "ID первой команды обязательно")
  @Schema(description = "Идентификатор первой команды", example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer team1Id;

  @NotNull(message = "ID второй команды обязательно")
  @Schema(description = "Идентификатор второй команды", example = "2",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer team2Id;

  @Schema(description = "Запланированная дата и время матча", example = "2024-02-15T18:00:00")
  private LocalDateTime scheduledDate;

  @Schema(description = "Формат матча: best_of_3_sets — до 2 побед, best_of_5_sets — до 3 побед",
      example = "best_of_3_sets", allowableValues = {"best_of_3_sets", "best_of_5_sets"})
  private String format;

  @Schema(description = "Место проведения матча", example = "Корт №1", maxLength = 255)
  private String location;

  @Schema(description = "Флаг завершенности матча", example = "false")
  private Boolean completed;
}
