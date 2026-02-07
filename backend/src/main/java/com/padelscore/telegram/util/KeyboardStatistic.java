package com.padelscore.telegram.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class KeyboardStatistic {

  public static final String MENU = "menu";

  public static final String PLAYER_RATING = "player_rating";

  public static final String RATING_DESCRIPTION = "rating_description";

  /**
   * Клавиатура экрана «Рейтинг игроков»: Описание расчета, Главное меню
   */
  public InlineKeyboardMarkup getPlayerRatingKeyboard() {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    keyboard.add(
        com.padelscore.util.KeyboardUtil.singleButtonRow("📋 Описание расчёта", RATING_DESCRIPTION));
    keyboard.add(com.padelscore.util.KeyboardUtil.singleButtonRow("📑 Главное меню", MENU));
    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Клавиатура экрана «Описание расчёта рейтинга»: только Назад к рейтингу
   */
  public InlineKeyboardMarkup getRatingDescriptionBackKeyboard() {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    keyboard.add(
        com.padelscore.util.KeyboardUtil.singleButtonRow("◀️ Назад к рейтингу", PLAYER_RATING));
    markup.setKeyboard(keyboard);
    return markup;
  }
}
