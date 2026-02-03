package com.padelscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на аутентификацию через Telegram Web App")
public class TelegramAuthRequest {

  @NotBlank(message = "initData обязателен")
  @Schema(description = "Данные инициализации Telegram Web App (initData), "
      + "содержащие информацию о пользователе и подпись",
      example = "query_id=AAHdF6IQAAAAAN0XohDhrOrc&user=%22%3A%22vd...",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String initData;
}
