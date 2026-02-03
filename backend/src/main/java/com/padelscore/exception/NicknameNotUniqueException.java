package com.padelscore.exception;

/**
 * Исключение при попытке создать/обновить профиль с ником, который уже занят (сравнение без учёта регистра).
 */
public class NicknameNotUniqueException extends RuntimeException {

  public NicknameNotUniqueException(String message) {
    super(message);
  }
}
