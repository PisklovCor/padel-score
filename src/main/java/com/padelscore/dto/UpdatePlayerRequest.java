package com.padelscore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlayerRequest {
    @NotBlank(message = "Имя обязательно")
    private String firstName;
    
    private String lastName;
    
    private String nickname;
    
    private Long telegramId;
    
    private Integer rating;
    
    private String position;
}
