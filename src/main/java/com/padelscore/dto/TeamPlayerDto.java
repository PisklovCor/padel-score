package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO связи игрок–команда (TeamPlayer) с данными профиля. Используется в ответах API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Игрок в составе команды: связь TeamPlayer + данные профиля")
public class TeamPlayerDto {

  @Schema(description = "Идентификатор записи TeamPlayer", example = "1",
      accessMode = Schema.AccessMode.READ_ONLY)
  private Integer id;

  @Schema(description = "Идентификатор команды", example = "1")
  private Integer teamId;

  @Schema(description = "Позиция в команде: primary, reserve, coach", example = "primary",
      allowableValues = {"primary", "reserve", "coach"})
  private String position;

  @Schema(description = "Идентификатор профиля игрока", example = "1")
  private Integer playerProfileId;

  @Schema(description = "Имя игрока", example = "Иван", maxLength = 255)
  private String firstName;

  @Schema(description = "Фамилия игрока", example = "Петров", maxLength = 255)
  private String lastName;

  @Schema(description = "Псевдоним в Telegram", example = "ivan_padel", maxLength = 255)
  private String nickname;

  @Schema(description = "Telegram user ID", example = "123456789")
  private Long telegramId;

  @Schema(description = "Рейтинг игрока", example = "1500")
  private Integer rating;
}
