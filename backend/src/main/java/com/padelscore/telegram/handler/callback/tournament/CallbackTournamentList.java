package com.padelscore.telegram.handler.callback.tournament;

import java.util.List;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.TournamentDto;
import com.padelscore.service.PlayerProfileService;
import com.padelscore.service.TournamentService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import com.padelscore.telegram.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTournamentList implements Callback {

  private final TournamentService tournamentService;
  private final KeyboardTournamentUtil keyboardTournamentUtil;
  private final KeyboardUtil keyboardUtil;
  private final PlayerProfileService playerProfileService;

  /**
   * Совпадение для callback data «tournaments» или «tournament_list».
   */
  @Override
  public boolean coincidence(String command) {
    //TODO: после раздела UI посмотреть может быть они одинаковые
    return "tournaments".equals(command) || "tournament_list".equals(command);
  }

  /**
   * Редактирует сообщение: список турниров (участие команд пользователя) и клавиатуру с турнирами.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    try {
      if (!playerProfileService.existsByTelegramId(userId)) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(
            "⚠️ У вас пока нет профиля.\n\nВоспользуйтесь пунктом меню для создания профиля.");
        message.setReplyMarkup(keyboardUtil.getButtonToMenu());
        bot.execute(message);
        return;
      }

      Integer playerProfileId = playerProfileService.getPlayerProfileByTelegramId(userId).getId();
      List<TournamentDto> tournaments =
          tournamentService.getTournamentsByUserTeams(playerProfileId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      if (tournaments.isEmpty()) {
        message.setText("🏆 Турниры\n\nВаши команды пока не участвуют ни в одном турнире.");
        message.setReplyMarkup(keyboardUtil.getButtonToMenu());
      } else {
        StringBuilder text = new StringBuilder("🏆 Турниры (участие ваших команд)\n\n");
        for (TournamentDto t : tournaments) {
          text.append(String.format("• %s (ID: %d)\n", t.getTitle(), t.getId()));
        }
        message.setText(text.toString());
        message.setReplyMarkup(keyboardTournamentUtil.getTournamentsMenu(tournaments));
      }
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }
}
