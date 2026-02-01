package com.padelscore.telegram.handler;

import com.padelscore.telegram.handler.command.Command;
import com.padelscore.telegram.util.KeyboardUtil;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.TeamDto;
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
    private final TeamPlayerService teamPlayerService;
    private final List<Command> commands;

    public void handle(Message message, TelegramLongPollingBot bot) {
        String text = message.getText();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();

        try {
            commands.stream()
                    .filter(c -> c.coincidence(text))
                    .findFirst()
                    .ifPresentOrElse(
                            c -> c.handle(message, bot),
                            () -> sendMessage(chatId,
                                    "Неизвестная команда. Используйте /help для справки.", bot
                            )
                    );

//            if (text.startsWith("/start")) {
//                handleStart(chatId, bot);
//            } else if (text.startsWith("/create_tournament")) {
//                handleCreateTournament(chatId, userId, bot, text);
//            } else if (text.startsWith("/my_tournaments")) {
//                handleMyTournaments(chatId, userId, bot);
//            } else if (text.startsWith("/add_team")) {
//                handleAddTeam(chatId, userId, bot, text);
//            } else if (text.startsWith("/add_player")) {
//                handleAddPlayer(chatId, userId, bot, text);
//            } else if (text.startsWith("/add_match")) {
//                handleAddMatch(chatId, userId, bot, text);
//            } else if (text.startsWith("/help")) {
//                handleHelp(chatId, bot);
//            } else {
//                sendMessage(chatId, "Неизвестная команда. Используйте /help для справки.", bot);
//            }
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
                "Используйте команды или меню ниже для навигации.\n\n" +
                "Для создания турнира используйте:\n" +
                "/create_tournament Название";
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboardUtil.getMenu());
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
                    tournamentTitle, null, userId, "group", "points", "prize", null, false);
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
    
    private void handleAddTeam(Long chatId, Long userId, TelegramLongPollingBot bot, String commandText) {
        String[] parts = commandText.split(" ", 3);
        if (parts.length < 3 || parts[2].trim().isEmpty()) {
            String text = "Для добавления команды используйте формат:\n" +
                    "/add_team ID_турнира Название команды\n\n" +
                    "Пример:\n" +
                    "/add_team 1 Команда А";
            sendMessage(chatId, text, bot);
            return;
        }
        
        try {
            Integer tournamentId = Integer.parseInt(parts[1].trim());
            String teamName = parts[2].trim();
            
            TeamDto team = teamService.createTeam(tournamentId, teamName, userId, null, null);
            String text = String.format("✅ Команда \"%s\" успешно добавлена в турнир!\n\nID: %d",
                    team.getName(), team.getId());
            sendMessage(chatId, text, bot);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Ошибка: неверный формат ID турнира.", bot);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при создании команды: " + e.getMessage(), bot);
        }
    }
    
    private void handleAddPlayer(Long chatId, Long userId, TelegramLongPollingBot bot, String commandText) {
        String[] parts = commandText.split(" ", 3);
        if (parts.length < 3 || parts[2].trim().isEmpty()) {
            String text = "Для добавления игрока используйте формат:\n" +
                    "/add_player ID_команды Имя Фамилия\n\n" +
                    "Пример:\n" +
                    "/add_player 1 Иван Иванов";
            sendMessage(chatId, text, bot);
            return;
        }
        
        try {
            Integer teamId = Integer.parseInt(parts[1].trim());
            String[] nameParts = parts[2].trim().split(" ", 2);
            
            if (nameParts.length < 2) {
                sendMessage(chatId, "Ошибка: укажите имя и фамилию игрока.", bot);
                return;
            }
            
            String firstName = nameParts[0];
            String lastName = nameParts[1];
            
            TeamPlayerDto player = teamPlayerService.createPlayer(teamId, firstName, lastName, userId, null, "primary");
            String text = String.format("✅ Игрок \"%s %s\" успешно добавлен в команду!\n\nID: %d",
                    player.getFirstName(), player.getLastName(), player.getId());
            sendMessage(chatId, text, bot);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Ошибка: неверный формат ID команды.", bot);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при добавлении игрока: " + e.getMessage(), bot);
        }
    }
    
    private void handleAddMatch(Long chatId, Long userId, TelegramLongPollingBot bot, String commandText) {
        String[] parts = commandText.split(" ");
        if (parts.length < 4) {
            String text = "Для создания матча используйте формат:\n" +
                    "/add_match ID_турнира ID_команды1 ID_команды2\n\n" +
                    "Пример:\n" +
                    "/add_match 1 1 2";
            sendMessage(chatId, text, bot);
            return;
        }
        
        try {
            Integer tournamentId = Integer.parseInt(parts[1].trim());
            Integer team1Id = Integer.parseInt(parts[2].trim());
            Integer team2Id = Integer.parseInt(parts[3].trim());
            
            if (team1Id.equals(team2Id)) {
                sendMessage(chatId, "Ошибка: команды не могут быть одинаковыми.", bot);
                return;
            }
            
            MatchDto match = matchService.createMatch(
                    tournamentId, team1Id, team2Id, null, null, null, false);
            String text = String.format("✅ Матч успешно создан!\n\nID: %d\nСтатус: %s",
                    match.getId(), match.getStatus());
            sendMessage(chatId, text, bot);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Ошибка: неверный формат параметров.", bot);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при создании матча: " + e.getMessage(), bot);
        }
    }
    
//    private void handleHelp(Long chatId, TelegramLongPollingBot bot) {
//        String text = "📖 Справка по командам:\n\n" +
//                "/start - Главное меню\n" +
//                "/create_tournament Название - Создать турнир\n" +
//                "/my_tournaments - Мои турниры\n" +
//                "/add_team ID_турнира Название - Добавить команду\n" +
//                "/add_player ID_команды Имя Фамилия - Добавить игрока\n" +
//                "/add_match ID_турнира ID_команды1 ID_команды2 - Создать матч\n" +
//                "/help - Эта справка\n\n" +
//                "Используйте inline-кнопки для быстрого доступа к функциям.";
//        sendMessage(chatId, text, bot);
//    }
    
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
