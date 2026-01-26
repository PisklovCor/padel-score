package com.padelscore.http.controller;

import com.padelscore.dto.TelegramAuthRequest;
import com.padelscore.service.TelegramAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final TelegramAuthService telegramAuthService;
    
    @PostMapping("/telegram")
    public ResponseEntity<Map<String, Object>> verifyTelegram(@Valid @RequestBody TelegramAuthRequest request) {
        Map<String, Object> result = telegramAuthService.verifyAndGetUser(request.getInitData());
        return ResponseEntity.ok(result);
    }
}
