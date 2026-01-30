package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание профиля игрока (PlayerProfile)")
public class CreatePlayerProfileRequest {

  @NotBlank(message = "Имя обязательно")
  @Size(min = 1, max = 255)
  @Schema(description = "Имя игрока", example = "Иван",
      requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
  private String firstName;

  @Size(max = 255)
  @Schema(description = "Фамилия игрока", example = "Петров", maxLength = 255)
  private String lastName;

  @NotBlank(message = "Псевдоним (никнейм) обязателен")
  @Size(min = 1, max = 255)
  @Schema(description = "Псевдоним (никнейм) в Telegram", example = "ivan_padel",
      requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
  private String nickname;

  @NotNull(message = "Telegram user ID обязателен")
  @Schema(description = "Telegram user ID для уведомлений", example = "123456789",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long telegramId;

  @Min(0)
  @Max(9999)
  @Schema(description = "Рейтинг игрока (0–9999)", example = "1500")
  private Integer rating;
}
