package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.service.PlayerProfileService;
import com.padelscore.telegram.handler.command.Command;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandDeletePlayerProfile implements Command {

    private final PlayerProfileService playerProfileService;
    private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

    @Override
    public boolean coincidence(String command) {

        return "/delete_profiles".equals(command);
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {

        final Long userId = message.getFrom().getId();

        var playerProfileDto = playerProfileService.getPlayerProfileByTelegramId(userId);

        playerProfileService.deletePlayerProfile(playerProfileDto.getId());

        var messageReply = new SendMessage();
        messageReply.setChatId(message.getChatId().toString());
        messageReply.setText("❌ Ваш профиль удален.");
        messageReply.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(false));

        try {
            bot.execute(messageReply);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
