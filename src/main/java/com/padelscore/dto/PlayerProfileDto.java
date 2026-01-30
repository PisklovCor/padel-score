package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для профиля игрока (PlayerProfile). Используется в ответах API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Профиль игрока: имя, рейтинг, привязка к Telegram")
public class PlayerProfileDto {

  @Schema(description = "Идентификатор профиля игрока", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Integer id;

  @Schema(description = "Имя игрока", example = "Иван", maxLength = 255)
  private String firstName;

  @Schema(description = "Фамилия игрока", example = "Петров", maxLength = 255)
  private String lastName;

  @Schema(description = "Псевдоним (никнейм) в Telegram", example = "ivan_padel", maxLength = 255)
  private String nickname;

  @Schema(description = "Telegram user ID", example = "123456789")
  private Long telegramId;

  @Schema(description = "Рейтинг игрока (0–9999)", example = "1500")
  private Integer rating;
}
