package com.padelscore.http.controller;

import com.padelscore.dto.TelegramAuthRequest;
import com.padelscore.service.TelegramAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для аутентификации через Telegram")
public class AuthController {
    
    private final TelegramAuthService telegramAuthService;
    
    @PostMapping("/telegram")
    @Operation(summary = "Верификация Telegram пользователя", 
               description = "Проверяет данные авторизации Telegram и возвращает информацию о пользователе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная верификация",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
        @ApiResponse(responseCode = "401", description = "Неверная подпись данных")
    })
    public ResponseEntity<Map<String, Object>> verifyTelegram(@Valid @RequestBody TelegramAuthRequest request) {
        Map<String, Object> result = telegramAuthService.verifyAndGetUser(request.getInitData());
        return ResponseEntity.ok(result);
    }
}
