package com.padelscore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerRequest {
    @NotNull(message = "ID команды обязателен")
    private Integer teamId;
    
    @NotBlank(message = "Имя обязательно")
    private String firstName;
    
    private String lastName;
    
    private Long telegramId;
    
    private Integer rating;
    
    private String position; // 'primary', 'reserve', 'coach'
}
