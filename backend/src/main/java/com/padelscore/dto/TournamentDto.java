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
@Schema(description = "Информация о турнире")
public class TournamentDto {

  @Schema(description = "Идентификатор турнира", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Integer id;

  @Schema(description = "Название турнира", example = "Весенний турнир 2024", maxLength = 255)
  private String title;

  @Schema(description = "Описание турнира", example = "Ежегодный весенний турнир по паделу", maxLength = 2000)
  private String description;

  @Schema(description = "Идентификатор профиля игрока-создателя турнира", example = "1")
  private Integer createdByPlayerProfileId;

  @Schema(description = "Дата начала турнира", example = "2024-03-01T10:00:00")
  private LocalDateTime startDate;

  @Schema(description = "Дата окончания турнира", example = "2024-03-31T20:00:00")
  private LocalDateTime endDate;

  @Schema(description = "Формат турнира: group — групповой, knockout — на выбывание, mixed — смешанный",
      example = "group", allowableValues = {"group", "knockout", "mixed"})
  private String format;

  @Schema(description = "Система подсчета очков: points — по очкам, win_loss — по победам/поражениям",
      example = "points", allowableValues = {"points", "win_loss"})
  private String scoringSystem;

  @Schema(description = "Призовой фонд или описание призов", example = "10000 рублей", maxLength = 500)
  private String prize;

  @Schema(description = "Статус турнира: PLANNED — запланирован, ACTIVE — активен, "
      + "COMPLETED — завершен, CANCELLED — отменен",
      example = "ACTIVE", allowableValues = {"PLANNED", "ACTIVE", "COMPLETED", "CANCELLED"})
  private String status;

  @Schema(description = "Флаг завершенности турнира", example = "false")
  private Boolean completed;
}
