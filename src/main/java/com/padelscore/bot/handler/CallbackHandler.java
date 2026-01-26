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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallbackHandler {
    
    private final TournamentService tournamentService;
    private final TeamService teamService;
    private final MatchService matchService;
    private final StatisticsService statisticsService;
    private final KeyboardUtil keyboardUtil;
    
    public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long userId = callbackQuery.getFrom().getId();
        
        try {
            if (data.startsWith("tournament_")) {
                handleTournamentCallback(data, chatId, messageId, userId, bot);
            } else if (data.startsWith("team_")) {
                handleTeamCallback(data, chatId, messageId, userId, bot);
            } else if (data.startsWith("match_")) {
                handleMatchCallback(data, chatId, messageId, userId, bot);
            } else if (data.equals("main_menu")) {
                handleMainMenu(chatId, messageId, bot);
            } else if (data.startsWith("leaderboard_")) {
                handleLeaderboard(data, chatId, messageId, bot);
            } else if (data.startsWith("help")) {
                handleHelp(chatId, bot);
            }
            
            bot.execute(org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .build());
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка: " + e.getMessage(), bot);
        }
    }
    
    private void handleTournamentCallback(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        if (data.equals("tournament_list")) {
            List<TournamentDto> tournaments = tournamentService.getTournamentsByUser(userId);
            EditMessageText message = new EditMessageText();
            message.setChatId(chatId.toString());
            message.setMessageId(messageId);
            if (tournaments.isEmpty()) {
                message.setText("У вас пока нет турниров.");
            } else {
                StringBuilder text = new StringBuilder("🏆 Ваши турниры:\n\n");
                for (TournamentDto tournament : tournaments) {
                    text.append(String.format("• %s (ID: %d)\n", tournament.getTitle(), tournament.getId()));
                }
                message.setText(text.toString());
                message.setReplyMarkup(keyboardUtil.getTournamentsMenu(tournaments));
            }
            bot.execute(message);
        } else if (data.startsWith("tournament_")) {
            Integer tournamentId = Integer.parseInt(data.split("_")[1]);
            TournamentDto tournament = tournamentService.getTournament(tournamentId);
            
            EditMessageText message = new EditMessageText();
            message.setChatId(chatId.toString());
            message.setMessageId(messageId);
            message.setText(String.format("🏆 Турнир: %s\n\nID: %d\nФормат: %s", 
                    tournament.getTitle(), tournament.getId(), tournament.getFormat()));
            message.setReplyMarkup(keyboardUtil.getTournamentMenu(tournamentId));
            bot.execute(message);
        }
    }
    
    private void handleTeamCallback(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        // Обработка команд
    }
    
    private void handleMatchCallback(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        // Обработка матчей
    }
    
    private void handleLeaderboard(String data, Long chatId, Integer messageId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer tournamentId = Integer.parseInt(data.split("_")[1]);
        List<LeaderboardEntryDto> leaderboard = statisticsService.getLeaderboard(tournamentId);
        
        StringBuilder text = new StringBuilder("📊 Турнирная таблица:\n\n");
        text.append(String.format("%-3s %-20s %-4s %-4s %-4s %-8s %-6s\n", 
                "#", "Команда", "И", "В", "П", "Сеты", "Очки"));
        text.append("─".repeat(50)).append("\n");
        
        int position = 1;
        for (LeaderboardEntryDto entry : leaderboard) {
            text.append(String.format("%-3d %-20s %-4d %-4d %-4d %-8s %-6d\n",
                    position++,
                    entry.getTeamName().length() > 20 ? entry.getTeamName().substring(0, 17) + "..." : entry.getTeamName(),
                    entry.getMatches(),
                    entry.getWins(),
                    entry.getLosses(),
                    entry.getSetsWon() + "-" + entry.getSetsLost(),
                    entry.getPoints()));
        }
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text.toString());
        message.setReplyMarkup(keyboardUtil.getBackToTournamentMenu(tournamentId));
        bot.execute(message);
    }
    
    private void handleMainMenu(Long chatId, Integer messageId, TelegramLongPollingBot bot) throws TelegramApiException {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText("🏆 Главное меню PadelScore Bot");
        message.setReplyMarkup(keyboardUtil.getMainMenu());
        bot.execute(message);
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

    private void handleHelp(Long chatId, TelegramLongPollingBot bot) {
        String text = "📖 Справка по командам:\n\n" +
                "/start - Главное меню\n" +
                "/create_tournament Название - Создать турнир\n" +
                "/my_tournaments - Мои турниры\n" +
                "/help - Эта справка\n\n" +
                "Используйте inline-кнопки для быстрого доступа к функциям.";
        sendMessage(chatId, text, bot);
    }
}
