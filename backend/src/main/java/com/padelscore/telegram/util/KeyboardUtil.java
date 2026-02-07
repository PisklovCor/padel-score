package com.padelscore.telegram.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardUtil {

  public static final String TOURNAMENTS = "tournaments";

  public static final String MY_TEAMS = "my_teams";

  public static final String PROFILES = "profiles";

  public static final String HELP = "help";

  public static final String MENU = "menu";

  public static final String PLAYER_RATING = "player_rating";

  /**
   * Добавляет кнопки главного меню
   *
   * @return главное меню
   */
  public InlineKeyboardMarkup getMenu() {
    InlineKeyboardMarkup markupMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardMenu = new ArrayList<>();

    keyboardMenu.add(com.padelscore.util.KeyboardUtil.singleButtonRow("🏆 Турниры", TOURNAMENTS));
    keyboardMenu.add(com.padelscore.util.KeyboardUtil.singleButtonRow("🎭 Мои команды", MY_TEAMS));
    keyboardMenu.add(
        com.padelscore.util.KeyboardUtil.singleButtonRow("📊 Рейтинг игроков", PLAYER_RATING));
    keyboardMenu.add(com.padelscore.util.KeyboardUtil.singleButtonRow("👤 Профиль", PROFILES));
    keyboardMenu.add(com.padelscore.util.KeyboardUtil.singleButtonRow("❓ Помощь", HELP));
    markupMenu.setKeyboard(keyboardMenu);
    return markupMenu;
  }

  /**
   * Добавляет единственную кнопку перехода в главное меню
   *
   * @return главное меню
   */
  public InlineKeyboardMarkup getButtonToMenu() {
    InlineKeyboardMarkup markupButtonToMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardButtonToMenu = new ArrayList<>();

    keyboardButtonToMenu.add(com.padelscore.util.KeyboardUtil.singleButtonRow("📑 Главное меню", MENU));
    markupButtonToMenu.setKeyboard(keyboardButtonToMenu);
    return markupButtonToMenu;
  }
}
