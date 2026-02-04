package com.padelscore.telegram.handler.callback.team;

import com.padelscore.dto.TeamDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.service.TeamPlayerService;
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

/**
 * Обработчик выбора команды из списка «Команды турнира»: показывает состав (ник имя — рейтинг) и
 * кнопку «Назад».
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTeamView implements Callback {

  private final TeamService teamService;
  private final TeamPlayerService teamPlayerService;
  private final KeyboardTournamentUtil keyboardUtil;

  /**
   * Совпадение для callback data вида «team_<id>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.matches("team_\\d+");
  }

  /**
   * Редактирует сообщение: состав команды (ник, имя, рейтинг) и кнопка «Назад к командам».
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();

    Integer teamId = Integer.parseInt(data.split("_")[1]);
    TeamDto team = teamService.getTeam(teamId);
    List<TeamPlayerDto> players = teamPlayerService.getPlayersByTeam(teamId);

    String text = buildTeamCompositionText(team, players);

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(text);
    message.setReplyMarkup(keyboardUtil.getTeamViewMenu(team.getTournamentId()));

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }

  private static String buildTeamCompositionText(TeamDto team, List<TeamPlayerDto> players) {
    StringBuilder sb = new StringBuilder();
    sb.append("👥 ").append(team.getName()).append("\n\n");
    sb.append("Состав:\n");
    if (players.isEmpty()) {
      sb.append("В команде пока нет игроков.\n");
    } else {
      for (TeamPlayerDto p : players) {
        String nick = p.getNickname() != null && !p.getNickname().isBlank()
            ? p.getNickname().trim() + " " : "";
        String name = p.getFirstName() != null && !p.getFirstName().isBlank()
            ? p.getFirstName().trim() : "—";
        String rating = p.getRating() != null ? String.valueOf(p.getRating()) : "—";
        sb.append("• ").append(nick).append(name).append(" — ").append(rating).append("\n");
      }
    }
    return sb.toString();
  }
}
