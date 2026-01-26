package com.padelscore.bot.handler;

import com.padelscore.bot.util.KeyboardUtil;
import com.padelscore.dto.LeaderboardEntryDto;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.TournamentDto;
import com.padelscore.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    
    private final TournamentService tournamentService;
    private final TeamService teamService;
    private final MatchService matchService;
    private final StatisticsService statisticsService;
    private final KeyboardUtil keyboardUtil;
    
    public void handle(Message message, TelegramLongPollingBot bot) {
        String text = message.getText();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        
        try {
            if (text.startsWith("/start")) {
                handleStart(chatId, bot);
            } else if (text.startsWith("/create_tournament")) {
                handleCreateTournament(chatId, userId, bot, text);
            } else if (text.startsWith("/my_tournaments")) {
                handleMyTournaments(chatId, userId, bot);
            } else if (text.startsWith("/help")) {
                handleHelp(chatId, bot);
            } else {
                sendMessage(chatId, "Неизвестная команда. Используйте /help для справки.", bot);
            }
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка: " + e.getMessage(), bot);
        }
    }
    
    private void handleStart(Long chatId, TelegramLongPollingBot bot) {
        String text = "🏆 Добро пожаловать в PadelScore Bot!\n\n" +
                "Я помогу вам управлять турнирами по паделу:\n" +
                "• Создавать турниры и команды\n" +
                "• Вводить результаты матчей\n" +
                "• Просматривать таблицу и статистику\n\n" +
                "Используйте меню ниже для навигации.";
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboardUtil.getMainMenu());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private void handleCreateTournament(Long chatId, Long userId, TelegramLongPollingBot bot, String commandText) {
        String[] parts = commandText.split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            String text = "Для создания турнира используйте формат:\n" +
                    "/create_tournament Название турнира\n\n" +
                    "Пример:\n" +
                    "/create_tournament Клубный чемпионат 2025";
            sendMessage(chatId, text, bot);
            return;
        }
        
        String tournamentTitle = parts[1].trim();
        try {
            TournamentDto tournament = tournamentService.createTournament(
                    tournamentTitle, null, userId, "group", "points");
            String text = String.format("✅ Турнир \"%s\" успешно создан!\n\nID: %d\n\nИспользуйте /my_tournaments для управления.", 
                    tournament.getTitle(), tournament.getId());
            sendMessage(chatId, text, bot);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при создании турнира: " + e.getMessage(), bot);
        }
    }
    
    private void handleMyTournaments(Long chatId, Long userId, TelegramLongPollingBot bot) {
        List<TournamentDto> tournaments = tournamentService.getTournamentsByUser(userId);
        if (tournaments.isEmpty()) {
            sendMessage(chatId, "У вас пока нет турниров. Создайте новый через /create_tournament", bot);
        } else {
            StringBuilder text = new StringBuilder("🏆 Ваши турниры:\n\n");
            for (TournamentDto tournament : tournaments) {
                text.append(String.format("• %s (ID: %d)\n", tournament.getTitle(), tournament.getId()));
            }
            text.append("\nИспользуйте inline-кнопки для управления турниром.");
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text.toString());
            message.setReplyMarkup(keyboardUtil.getTournamentsMenu(tournaments));
            try {
                bot.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleHelp(Long chatId, TelegramLongPollingBot bot) {
        String text = "📖 Справка по командам:\n\n" +
                "/start - Главное меню\n" +
                "/create_tournament Название - Создать турнир\n" +
                "/my_tournaments - Мои турниры\n" +
                "/help - Эта справка\n\n" +
                "Используйте inline-кнопки для быстрого доступа к функциям.";
        sendMessage(chatId, text, bot);
    }
    
    private void sendMessage(Long chatId, String text, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
