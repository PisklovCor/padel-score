package com.padelscore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для профиля игрока (PlayerProfile)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfileDto {
    private Integer id; // ID профиля игрока
    private String firstName;
    private String lastName;
    private String nickname;
    private Long telegramId;
    private Integer rating;
}
