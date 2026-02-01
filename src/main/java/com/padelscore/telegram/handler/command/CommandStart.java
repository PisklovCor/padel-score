package com.padelscore.telegram.handler.command;

import com.padelscore.telegram.util.KeyboardUtil;
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
public class CommandStart implements Command {

    private final KeyboardUtil keyboardUtil;

    @Override
    public boolean coincidence(String command) {

        return "/start".equals(command);
    }

    @Override
    public void handle(Message message, TelegramLongPollingBot bot) {

        final var text = """
                🏆 Добро пожаловать в PadelScore Bot!
                Я помогу вам управлять турнирами по паделу:
                • Создавать турниры и команды
                • Вводить результаты матчей
                • Просматривать таблицу и статистику
                Используйте команды или меню ниже для навигации.""";

        var messageReply = new SendMessage();
        messageReply.setChatId(message.getChatId().toString());
        messageReply.setText(text);
        messageReply.setReplyMarkup(keyboardUtil.getMainMenu());

        try {
            bot.execute(messageReply);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
