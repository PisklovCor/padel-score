package com.padelscore.service;

import com.padelscore.util.TelegramAuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramAuthService {
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    /**
     * Верифицирует Telegram initData и возвращает информацию о пользователе
     */
    public Map<String, Object> verifyAndGetUser(String initData) {
        if (!TelegramAuthUtil.verifyInitData(initData, botToken)) {
            throw new RuntimeException("Invalid Telegram initData");
        }
        
        Long userId = TelegramAuthUtil.extractUserId(initData);
        if (userId == null) {
            throw new RuntimeException("Could not extract user ID from initData");
        }
        
        return Map.of(
                "userId", userId,
                "authenticated", true
        );
    }
}
