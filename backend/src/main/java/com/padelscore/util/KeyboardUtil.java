package com.padelscore.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class KeyboardUtil {


  /**
   * Создает ряд из одной inline‑кнопки Telegram.
   *
   * @param text         текст на кнопке
   * @param callbackData callback‑данные, которые придут боту при нажатии
   * @return неизменяемый список с одной кнопкой
   */
  public List<InlineKeyboardButton> singleButtonRow(String text, String callbackData) {
    InlineKeyboardButton button = createButton(text, callbackData);
    return List.of(button);
  }

  /**
   * Создает inline‑кнопку Telegram с заданным текстом и callback‑данными.
   *
   * @param text         текст, который будет отображаться на кнопке
   * @param callbackData значение callbackData, которое Telegram пришлёт боту при нажатии на кнопку
   * @return сконфигурированный {@link InlineKeyboardButton}
   */
  public InlineKeyboardButton createButton(String text, String callbackData) {
    InlineKeyboardButton button = new InlineKeyboardButton();
    button.setText(text);
    button.setCallbackData(callbackData);
    return button;
  }
}
