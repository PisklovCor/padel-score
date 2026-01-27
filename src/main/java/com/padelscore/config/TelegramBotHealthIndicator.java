package com.padelscore.config;

import com.padelscore.telegram.PadelScoreBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBotHealthIndicator implements HealthIndicator {

    private final PadelScoreBot padelScoreBot;

    @Override
    public Health health() {
        try {
            GetMe getMe = new GetMe();
            var botInfo = padelScoreBot.execute(getMe);
            
            return Health.up()
                    .withDetail("bot.username", botInfo.getUserName())
                    .withDetail("bot.id", botInfo.getId())
                    .withDetail("bot.firstName", botInfo.getFirstName())
                    .withDetail("bot.canJoinGroups", botInfo.getCanJoinGroups())
                    .withDetail("bot.canReadAllGroupMessages", botInfo.getCanReadAllGroupMessages())
                    .build();
        } catch (TelegramApiException e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getSimpleName())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
