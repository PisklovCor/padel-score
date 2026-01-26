package com.padelscore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Integer id;
    private Integer teamId;
    private String firstName;
    private String lastName;
    private Long telegramId;
    private Integer rating;
    private String position;
}
