package com.padelscore.telegram.handler.callback.team;

import com.padelscore.dto.TeamDto;
import com.padelscore.service.PlayerProfileService;
import com.padelscore.service.TeamService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardUtil;
import com.padelscore.util.MessageUtil;
import com.padelscore.util.ProfileRequiredGuard;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMyTeams implements Callback {

  private final TeamService teamService;

  private final KeyboardUtil keyboardUtil;

  private final PlayerProfileService playerProfileService;

  private final ProfileRequiredGuard profileRequiredGuard;

  /**
   * Совпадение для callback data «my_teams».
   */
  @Override
  public boolean coincidence(String command) {
    return "my_teams".equals(command);
  }

  /**
   * Редактирует сообщение: список команд пользователя (капитан/игрок) или предложение создать
   * профиль.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();
    final var userTelegramId = callbackQuery.getFrom().getId();

    try {
      if (profileRequiredGuard.requireProfileForCallback(userTelegramId, callbackQuery, bot)) {
        return;
      }
      String text = buildListText(userTelegramId);
      bot.execute(MessageUtil.createdEditMessageText(chatId, messageId, text,
          keyboardUtil.getButtonToMenu()));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private String buildListText(Long userTelegramId) {
    Integer playerProfileId = playerProfileService.getPlayerProfileByTelegramId(userTelegramId)
        .getId();
    List<TeamDto> teams = teamService.getTeamsByUser(playerProfileId);

    if (teams.isEmpty()) {
      return "👥 Мои команды\n\nВы пока не участвуете ни в одной команде (ни как капитан, ни как игрок).";
    }

    StringBuilder sb = new StringBuilder("👥 Мои команды\n\n");
    for (TeamDto team : teams) {
      String role = playerProfileId.equals(team.getCaptainPlayerProfileId()) ? "капитан" : "игрок";
      sb.append(String.format("• %s — %s (ID: %d)%n", team.getName(), role, team.getId()));
    }
    return sb.toString();
  }
}
