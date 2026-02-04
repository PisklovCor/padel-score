package com.padelscore.telegram.handler.callback.match;

import java.util.List;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.TeamDto;
import com.padelscore.service.TeamService;
import com.padelscore.telegram.handler.callback.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMatchCreate implements Callback {

  private final TeamService teamService;

  /**
   * Совпадение для callback data «match_create_<tournamentId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("match_create_");
  }

  /**
   * Отправляет сообщение с инструкцией по созданию матча (/add_match) и списком команд турнира.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();

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

  private void sendMessage(String chatId, String text, TelegramLongPollingBot bot) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(text);
    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }
}
