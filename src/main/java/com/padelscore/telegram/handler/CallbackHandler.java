package com.padelscore.telegram.handler;

import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardUtil;
import com.padelscore.dto.LeaderboardEntryDto;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.TeamDto;
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
    private final TeamPlayerService teamPlayerService;
    private final List<Callback> callbacks;
    
    public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long userId = callbackQuery.getFrom().getId();
        
        try {

            callbacks.stream()
                    .filter(c -> c.coincidence(data))
                    .findFirst()
                    .ifPresentOrElse(
                            c -> c.handle(callbackQuery, bot),
                            () -> sendMessage(chatId,
                                    "Неизвестная команда. Используйте /help для справки.", bot
                            )
                    );

//            if (data.startsWith("tournament_")) {
//                handleTournamentCallback(data, chatId, messageId, userId, bot);
//            } else if (data.startsWith("teams_list_")) {
//                handleTeamsList(data, chatId, messageId, bot);
//            } else if (data.startsWith("team_")) {
//                handleTeamCallback(data, chatId, messageId, userId, bot);
//            } else if (data.startsWith("team_create_")) {
//                handleTeamCreate(data, chatId, userId, bot);
//            } else if (data.startsWith("players_list_")) {
//                handlePlayersList(data, chatId, messageId, bot);
//            } else if (data.startsWith("player_")) {
//                handlePlayerCallback(data, chatId, messageId, bot);
//            } else if (data.startsWith("player_create_")) {
//                handlePlayerCreate(data, chatId, userId, bot);
//            } else if (data.startsWith("matches_list_")) {
//                handleMatchesList(data, chatId, messageId, bot);
//            } else if (data.startsWith("match_")) {
//                handleMatchCallback(data, chatId, messageId, userId, bot);
//            } else if (data.startsWith("match_create_")) {
//                handleMatchCreate(data, chatId, userId, bot);
//            } else if (data.startsWith("match_result_")) {
//                handleMatchResultInput(data, chatId, messageId, userId, bot);
//            } else if (data.startsWith("result_quick_")) {
//                handleQuickResult(data, chatId, messageId, userId, bot);
//            } else if (data.startsWith("match_view_")) {
//                handleMatchView(data, chatId, messageId, bot);
//            } else if (data.startsWith("match_dispute_")) {
//                handleMatchDispute(data, chatId, messageId, userId, bot);
//            } else if (data.equals("main_menu")) {
//                handleMainMenu(chatId, messageId, bot);
//            } else if (data.startsWith("leaderboard_")) {
//                handleLeaderboard(data, chatId, messageId, bot);
//            } else if (data.startsWith("help")) {
//                handleHelp(chatId, bot);
//            }
            
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
            message.setText(String.format("🏆 Турнир: %s\n\nID: %d\nФормат: %s\nПриз: %s",
                    tournament.getTitle(), tournament.getId(), tournament.getFormat(), tournament.getPrize()));
            message.setReplyMarkup(keyboardUtil.getTournamentMenu(tournamentId));
            bot.execute(message);
        }
    }
    
    private void handleTeamCallback(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer teamId = Integer.parseInt(data.split("_")[1]);
        TeamDto team = teamService.getTeam(teamId);
        List<TeamPlayerDto> players = teamPlayerService.getPlayersByTeam(teamId);
        
        StringBuilder text = new StringBuilder("👥 Команда: ").append(team.getName()).append("\n\n");
        text.append("ID: ").append(team.getId()).append("\n");
        text.append("Капитан ID: ").append(team.getCaptainId()).append("\n");
        if (team.getDescription() != null) {
            text.append("Описание: ").append(team.getDescription()).append("\n");
        }
        text.append("\nИгроков: ").append(players.size());
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text.toString());
        message.setReplyMarkup(keyboardUtil.getTeamMenu(teamId, team.getTournamentId()));
        bot.execute(message);
    }
    
    private void handleTeamCreate(String data, Long chatId, Long userId, TelegramLongPollingBot bot) {
        Integer tournamentId = Integer.parseInt(data.split("_")[2]);
        String text = "Для создания команды отправьте сообщение в формате:\n\n" +
                "/add_team " + tournamentId + " Название команды\n\n" +
                "Пример:\n" +
                "/add_team " + tournamentId + " Команда А";
        sendMessage(chatId, text, bot);
    }
    
    private void handlePlayersList(String data, Long chatId, Integer messageId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer teamId = Integer.parseInt(data.split("_")[2]);
        List<TeamPlayerDto> players = teamPlayerService.getPlayersByTeam(teamId);
        TeamDto team = teamService.getTeam(teamId);
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        
        if (players.isEmpty()) {
            message.setText("В команде пока нет игроков.\n\nИспользуйте кнопку ниже, чтобы добавить игрока.");
        } else {
            StringBuilder text = new StringBuilder("👤 Игроки команды ").append(team.getName()).append(":\n\n");
            for (TeamPlayerDto player : players) {
                text.append(String.format("• %s %s", player.getFirstName(), player.getLastName()));
                if (player.getPosition() != null) {
                    text.append(" (").append(player.getPosition()).append(")");
                }
                text.append("\n");
            }
            message.setText(text.toString());
        }
        message.setReplyMarkup(keyboardUtil.getPlayersMenu(players, teamId));
        bot.execute(message);
    }
    
    private void handlePlayerCallback(String data, Long chatId, Integer messageId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer playerId = Integer.parseInt(data.split("_")[1]);
        TeamPlayerDto player = teamPlayerService.getTeamPlayer(playerId);
        
        StringBuilder text = new StringBuilder("👤 Игрок: ").append(player.getFirstName())
                .append(" ").append(player.getLastName()).append("\n\n");
        text.append("ID: ").append(player.getId()).append("\n");
        text.append("Команда ID: ").append(player.getTeamId()).append("\n");
        if (player.getPosition() != null) {
            text.append("Позиция: ").append(player.getPosition()).append("\n");
        }
        if (player.getRating() != null) {
            text.append("Рейтинг: ").append(player.getRating()).append("\n");
        }
        if (player.getTelegramId() != null) {
            text.append("Telegram ID: ").append(player.getTelegramId()).append("\n");
        }
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text.toString());
        TeamDto team = teamService.getTeam(player.getTeamId());
        message.setReplyMarkup(keyboardUtil.getBackToTournamentMenu(team.getTournamentId()));
        bot.execute(message);
    }
    
    private void handlePlayerCreate(String data, Long chatId, Long userId, TelegramLongPollingBot bot) {
        Integer teamId = Integer.parseInt(data.split("_")[2]);
        String text = "Для добавления игрока отправьте сообщение в формате:\n\n" +
                "/add_player " + teamId + " Имя Фамилия\n\n" +
                "Пример:\n" +
                "/add_player " + teamId + " Иван Иванов";
        sendMessage(chatId, text, bot);
    }
    
    private void handleMatchesList(String data, Long chatId, Integer messageId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer tournamentId = Integer.parseInt(data.split("_")[2]);
        List<MatchDto> matches = matchService.getMatchesByTournament(tournamentId);
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        
        if (matches.isEmpty()) {
            message.setText("В этом турнире пока нет матчей.\n\nИспользуйте кнопку ниже, чтобы создать матч.");
        } else {
            StringBuilder text = new StringBuilder("⚽ Матчи турнира:\n\n");
            for (MatchDto match : matches) {
                String status = "scheduled".equals(match.getStatus()) ? "⏰" : 
                               "completed".equals(match.getStatus()) ? "✅" : "🔄";
                text.append(String.format("%s %s vs %s\n", status, match.getTeam1Name(), match.getTeam2Name()));
            }
            message.setText(text.toString());
        }
        message.setReplyMarkup(keyboardUtil.getMatchesMenu(matches, tournamentId));
        bot.execute(message);
    }
    
    private void handleMatchCallback(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer matchId = Integer.parseInt(data.split("_")[1]);
        MatchDto match = matchService.getMatch(matchId);
        
        StringBuilder text = new StringBuilder("⚽ Матч: ").append(match.getTeam1Name())
                .append(" vs ").append(match.getTeam2Name()).append("\n\n");
        text.append("ID: ").append(match.getId()).append("\n");
        text.append("Статус: ").append(match.getStatus()).append("\n");
        text.append("Формат: ").append(match.getFormat()).append("\n");
        if (match.getScheduledDate() != null) {
            text.append("Дата: ").append(match.getScheduledDate()).append("\n");
        }
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text.toString());
        message.setReplyMarkup(keyboardUtil.getMatchMenu(matchId, match.getTournamentId(), match.getStatus()));
        bot.execute(message);
    }
    
    private void handleMatchCreate(String data, Long chatId, Long userId, TelegramLongPollingBot bot) {
        Integer tournamentId = Integer.parseInt(data.split("_")[2]);
        List<TeamDto> teams = teamService.getTeamsByTournament(tournamentId);
        
        if (teams.size() < 2) {
            sendMessage(chatId, "Для создания матча нужно минимум 2 команды в турнире.", bot);
            return;
        }
        
        StringBuilder text = new StringBuilder("Для создания матча отправьте сообщение в формате:\n\n");
        text.append("/add_match ").append(tournamentId).append(" ID_команды1 ID_команды2\n\n");
        text.append("Доступные команды:\n");
        for (TeamDto team : teams) {
            text.append(String.format("• %s (ID: %d)\n", team.getName(), team.getId()));
        }
        text.append("\nПример:\n");
        text.append("/add_match ").append(tournamentId).append(" ")
                .append(teams.get(0).getId()).append(" ").append(teams.get(1).getId());
        
        sendMessage(chatId, text.toString(), bot);
    }
    
    private void handleMatchResultInput(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer matchId = Integer.parseInt(data.split("_")[2]);
        MatchDto match = matchService.getMatch(matchId);
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText("Выберите результат матча:\n\n" + match.getTeam1Name() + " vs " + match.getTeam2Name());
        message.setReplyMarkup(keyboardUtil.getResultInputMenu(matchId));
        bot.execute(message);
    }
    
    private void handleQuickResult(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        String[] parts = data.split("_");
        Integer matchId = Integer.parseInt(parts[2]);
        String score = parts[3];
        
        try {
            matchService.submitResult(matchId, score, userId, null);
            MatchDto match = matchService.getMatch(matchId);
            
            EditMessageText message = new EditMessageText();
            message.setChatId(chatId.toString());
            message.setMessageId(messageId);
            message.setText("✅ Результат матча сохранен!\n\n" + 
                    match.getTeam1Name() + " vs " + match.getTeam2Name() + "\n" +
                    "Счет: " + score);
            message.setReplyMarkup(keyboardUtil.getMatchMenu(matchId, match.getTournamentId(), "completed"));
            bot.execute(message);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при сохранении результата: " + e.getMessage(), bot);
        }
    }
    
    private void handleMatchView(String data, Long chatId, Integer messageId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer matchId = Integer.parseInt(data.split("_")[2]);
        MatchDto match = matchService.getMatch(matchId);
        
        StringBuilder text = new StringBuilder("📊 Результат матча:\n\n");
        text.append(match.getTeam1Name()).append(" vs ").append(match.getTeam2Name()).append("\n");
        text.append("Статус: ").append(match.getStatus()).append("\n");
        
        if ("completed".equals(match.getStatus())) {
            try {
                com.padelscore.dto.MatchResultDto result = matchService.getMatchResult(matchId);
                text.append("\n🏆 Победитель: ").append(result.getWinnerTeamName()).append("\n");
                text.append("Счет: ").append(result.getFinalScore()).append("\n");
                text.append("Очки победителя: ").append(result.getWinnerPoints()).append("\n");
                text.append("Очки проигравшего: ").append(result.getLoserPoints());
            } catch (Exception e) {
                text.append("\n(Результат не найден)");
            }
        }
        
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text.toString());
        message.setReplyMarkup(keyboardUtil.getMatchMenu(matchId, match.getTournamentId(), match.getStatus()));
        bot.execute(message);
    }
    
    private void handleMatchDispute(String data, Long chatId, Integer messageId, Long userId, TelegramLongPollingBot bot) throws TelegramApiException {
        Integer matchId = Integer.parseInt(data.split("_")[2]);
        
        try {
            matchService.disputeResult(matchId);
            MatchDto match = matchService.getMatch(matchId);
            
            EditMessageText message = new EditMessageText();
            message.setChatId(chatId.toString());
            message.setMessageId(messageId);
            message.setText("⚠️ Результат матча помечен как спорный.\n\n" +
                    "Администратор турнира будет уведомлен.");
            message.setReplyMarkup(keyboardUtil.getMatchMenu(matchId, match.getTournamentId(), match.getStatus()));
            bot.execute(message);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка: " + e.getMessage(), bot);
        }
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
        message.setReplyMarkup(keyboardUtil.getMenu());
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
