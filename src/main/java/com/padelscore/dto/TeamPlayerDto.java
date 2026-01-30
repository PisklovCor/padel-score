package com.padelscore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для связи игрок-команда (TeamPlayer)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamPlayerDto {

  private Integer id; // ID TeamPlayer

  private Integer teamId; // ID команды

  private String position; // Позиция в команде

  // Данные профиля игрока
  private Integer playerProfileId; // ID профиля игрока

  private String firstName;

  private String lastName;

  private String nickname;

  private Long telegramId;

  private Integer rating;
}
