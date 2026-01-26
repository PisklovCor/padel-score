package com.padelscore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTournamentRequest {
    @NotBlank(message = "Название турнира обязательно")
    private String title;
    
    private String description;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String format;
    
    private String scoringSystem;
}
