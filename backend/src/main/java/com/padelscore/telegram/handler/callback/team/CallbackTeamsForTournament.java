package com.padelscore.telegram.handler.callback.team;

import java.util.List;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.TeamDto;
import com.padelscore.service.TeamService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTeamUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTeamsForTournament implements Callback {

  private final TeamService teamService;

  private final KeyboardTeamUtil keyboardTeamUtil;

  /**
   * Совпадение для callback data вида «teams_list_<tournamentId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("teams_list_");
  }

  /**
   * Редактирует сообщение: список команд турнира и клавиатуру с командами и «Назад».
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var data = callbackQuery.getData();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    Integer tournamentId = Integer.parseInt(data.split("_")[2]);
    List<TeamDto> teams = teamService.getTeamsByTournament(tournamentId);

    String text = teams.isEmpty()
        ? "👥 Команды турнира\n\nВ этом турнире пока нет команд.\n\nИспользуйте кнопку ниже, чтобы добавить команду."
        : buildTeamsListText(teams);

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(text);
    message.setReplyMarkup(keyboardTeamUtil.getTeamsMenu(teams, tournamentId));

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private static String buildTeamsListText(List<TeamDto> teams) {
    StringBuilder sb = new StringBuilder("👥 Команды турнира\n\n");
    for (TeamDto team : teams) {
      sb.append(String.format("• %s (ID: %d)%n", team.getName(), team.getId()));
    }
    return sb.toString();
  }
}
