package com.padelscore.telegram.handler.callback.tournament;

import com.padelscore.dto.TeamDto;
import com.padelscore.service.TeamService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTournamentTeams implements Callback {

  private final TeamService teamService;

  private final KeyboardTournamentUtil keyboardUtil;

  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("teams_list_");
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();

    Integer tournamentId = Integer.parseInt(data.split("_")[2]);
    List<TeamDto> teams = teamService.getTeamsByTournament(tournamentId);

    String text = teams.isEmpty()
        ? "👥 Команды турнира\n\nВ этом турнире пока нет команд.\n\nИспользуйте кнопку ниже, чтобы добавить команду."
        : buildTeamsListText(teams);

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(text);
    message.setReplyMarkup(keyboardUtil.getTeamsMenu(teams, tournamentId));

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }

  private static String buildTeamsListText(List<TeamDto> teams) {
    StringBuilder sb = new StringBuilder("👥 Команды турнира\n\n");
    for (TeamDto team : teams) {
      sb.append(String.format("• %s (ID: %d)\n", team.getName(), team.getId()));
    }
    return sb.toString();
  }
}
