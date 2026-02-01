package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание игрока в команде (TeamPlayer)")
public class CreatePlayerRequest {

  @NotNull(message = "ID команды обязателен")
  @Schema(description = "Идентификатор команды", example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer teamId;

  @NotBlank(message = "Имя обязательно")
  @Size(min = 1, max = 255)
  @Schema(description = "Имя игрока", example = "Иван",
      requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
  private String firstName;

  @Size(max = 255)
  @Schema(description = "Фамилия игрока", example = "Петров", maxLength = 255)
  private String lastName;

  @Schema(description = "Telegram user ID для уведомлений", example = "123456789")
  private Long telegramId;

  @Min(0)
  @Max(9999)
  @Schema(description = "Рейтинг игрока (0–9999)", example = "1500")
  private Integer rating;

  @Pattern(regexp = "^(primary|reserve|coach)?$",
      message = "Допустимые значения: primary, reserve, coach")
  @Size(max = 50)
  @Schema(description = "Позиция в команде: primary — основной, reserve — запасной, coach — тренер",
      example = "primary", allowableValues = {
      "primary", "reserve", "coach"}, maxLength = 50)
  private String position;
}
