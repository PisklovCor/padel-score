package com.padelscore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResultRequest {

  @NotBlank(message = "Финальный счет обязателен (формат: 2-0 или 2-1)")
  private String finalScore;

  @NotNull(message = "ID профиля пользователя, отправившего результат, обязателен")
  private Integer submittedByPlayerProfileId;

  private String notes;
}
