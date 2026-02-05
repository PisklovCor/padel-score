package com.padelscore.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtil {

  /**
   * Создаёт и настраивает объект {@link EditMessageText} для редактирования существующего сообщения
   * в чате Telegram.
   * <p>
   * Метод задаёт идентификатор чата, идентификатор редактируемого сообщения, новый текст и, при
   * необходимости, встроенную клавиатуру ({@link InlineKeyboardMarkup}).
   *
   * @param chatId               идентификатор чата, в котором нужно отредактировать сообщение
   * @param messageId            идентификатор сообщения, которое требуется изменить
   * @param text                 новый текст сообщения
   * @param inlineKeyboardMarkup объект клавиатуры, прикрепляемой к сообщению (может быть
   *                             {@code null})
   * @return сконфигурированный объект {@link EditMessageText}, готовый к отправке через Telegram
   * Bot API
   */
  public EditMessageText createdEditMessageText(String chatId, int messageId, String text,
      InlineKeyboardMarkup inlineKeyboardMarkup) {

    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setChatId(chatId);
    editMessageText.setMessageId(messageId);
    editMessageText.setText(text);
    editMessageText.setReplyMarkup(inlineKeyboardMarkup);
    return editMessageText;
  }

  /**
   * Создаёт и настраивает объект {@link SendMessage} для отправки сообщения в чат Telegram.
   * <p>
   * Метод задаёт идентификатор чата, текст сообщения и (при необходимости) встроенную клавиатуру
   * {@link InlineKeyboardMarkup}.
   *
   * @param chatId               идентификатор чата, в который будет отправлено сообщение
   * @param text                 текст сообщения, который нужно отправить
   * @param inlineKeyboardMarkup объект клавиатуры, прикрепляемой к сообщению (может быть
   *                             {@code null})
   * @return сконфигурированный объект {@link SendMessage}, готовый к отправке через Telegram Bot
   * API
   */
  public SendMessage createdSendMessage(String chatId, String text,
      InlineKeyboardMarkup inlineKeyboardMarkup) {

    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(text);

    if (inlineKeyboardMarkup != null) {
      sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }
    return sendMessage;
  }
}
