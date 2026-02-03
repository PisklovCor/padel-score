package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание турнира")
public class CreateTournamentRequest {

  @NotBlank(message = "Название турнира обязательно")
  @Size(max = 255)
  @Schema(description = "Название турнира", example = "Весенний турнир 2024",
      requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
  private String title;

  @Size(max = 2000)
  @Schema(description = "Описание турнира", example = "Ежегодный весенний турнир по паделу",
      maxLength = 2000)
  private String description;

  @NotNull(message = "ID профиля создателя обязателен")
  @Schema(description = "Идентификатор профиля игрока-создателя турнира", example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
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

  @Size(max = 500)
  @Schema(description = "Призовой фонд или описание призов", example = "10000 рублей",
      maxLength = 500)
  private String prize;

  @Schema(description = "Статус турнира: PLANNED — запланирован, ACTIVE — активен, "
      + "COMPLETED — завершен, CANCELLED — отменен",
      example = "PLANNED", allowableValues = {"PLANNED", "ACTIVE", "COMPLETED", "CANCELLED"})
  private String status;

  @Schema(description = "Флаг завершенности турнира", example = "false")
  private Boolean completed;
}
