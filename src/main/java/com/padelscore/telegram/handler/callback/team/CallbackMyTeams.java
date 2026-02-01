package com.padelscore.telegram.handler.callback.team;

import com.padelscore.dto.TeamDto;
import com.padelscore.service.TeamService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardUtil;
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
public class CallbackMyTeams implements Callback {

  private final TeamService teamService;

  private final KeyboardUtil keyboardUtil;

  @Override
  public boolean coincidence(String command) {
    return "my_teams".equals(command);
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    List<TeamDto> teams = teamService.getTeamsByUser(userId);
    String text = buildListText(teams, userId);

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(text);
    message.setReplyMarkup(keyboardUtil.getButtonToMainMenu());

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }

  private static String buildListText(List<TeamDto> teams, Long telegramId) {
    if (teams.isEmpty()) {
      return "👥 Мои команды\n\nВы пока не участвуете ни в одной команде (ни как капитан, ни как игрок).";
    }
    StringBuilder sb = new StringBuilder("👥 Мои команды\n\n");
    for (TeamDto team : teams) {
      String role = telegramId.equals(team.getCaptainId()) ? "капитан" : "игрок";
      sb.append(String.format("• %s — %s (ID: %d)\n", team.getName(), role, team.getId()));
    }
    return sb.toString();
  }
}
