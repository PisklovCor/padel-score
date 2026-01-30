package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.telegram.handler.command.Command;
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
public class CommandHelpPlayerProfile implements Command {

    @Override
    public boolean coincidence(String command) {

        return "/help_profiles".equals(command);
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {

        final var text = """
                📖 Справка по командам профиля:
                
                /profiles - Посмотреть профиль
                /create_profiles - Создать профиль
                /update_profiles - Обновить профиль
                /delete_profiles - Удалить профиль
                /help - Справка
                
                Используйте inline-кнопки для быстрого доступа к функциям.""";

        var messageReply  = new SendMessage();
        messageReply.setChatId(message.getChatId().toString());
        messageReply.setText(text);

        try {
            bot.execute(messageReply);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
