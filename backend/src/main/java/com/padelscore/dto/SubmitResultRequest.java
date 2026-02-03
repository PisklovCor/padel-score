package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на отправку результата матча")
public class SubmitResultRequest {

  @NotBlank(message = "Финальный счет обязателен (формат: 2-0 или 2-1)")
  @Pattern(regexp = "^[0-9]+-[0-9]+$", message = "Формат счета: число-число (например, 2-0)")
  @Schema(description = "Финальный счет матча в формате 'победные_сеты-проигранные_сеты' (например, 2-0 или 2-1)",
      example = "2-1", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "^[0-9]+-[0-9]+$", maxLength = 50)
  private String finalScore;

  @NotNull(message = "ID профиля пользователя, отправившего результат, обязателен")
  @Schema(description = "Идентификатор профиля игрока, отправившего результат", example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer submittedByPlayerProfileId;

  @Size(max = 1000)
  @Schema(description = "Дополнительные заметки о матче", example = "Отличная игра!", maxLength = 1000)
  private String notes;
}
