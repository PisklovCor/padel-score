package com.padelscore.telegram.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardUtil {

  /**
   * Добавляет кнопки главного меню
   *
   * @return главное меню
   */
  public InlineKeyboardMarkup getMenu() {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> row1 = new ArrayList<>();
    InlineKeyboardButton tournaments = new InlineKeyboardButton();
    tournaments.setText("🏆 Турниры");
    tournaments.setCallbackData("tournaments");
    row1.add(tournaments);

    List<InlineKeyboardButton> row2 = new ArrayList<>();
    InlineKeyboardButton teams = new InlineKeyboardButton();
    teams.setText("🎭 Мои команды");
    teams.setCallbackData("my_teams");
    row2.add(teams);

    List<InlineKeyboardButton> row3 = new ArrayList<>();
    InlineKeyboardButton profile = new InlineKeyboardButton();
    profile.setText("👤 Профиль");
    profile.setCallbackData("profiles");
    row3.add(profile);

    List<InlineKeyboardButton> row4 = new ArrayList<>();
    InlineKeyboardButton help = new InlineKeyboardButton();
    help.setText("❓ Помощь");
    help.setCallbackData("help");
    row4.add(help);

    keyboard.add(row1);
    keyboard.add(row2);
    keyboard.add(row3);
    keyboard.add(row4);
    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Добавляет единственную кнопку перехода в главное меню
   *
   * @return главное меню
   */
  public InlineKeyboardMarkup getButtonToMenu() {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> row = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("📑 Главное меню");
    back.setCallbackData("menu");
    row.add(back);
    keyboard.add(row);

    markup.setKeyboard(keyboard);
    return markup;
  }
}
