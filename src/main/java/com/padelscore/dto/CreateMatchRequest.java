package com.padelscore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMatchRequest {
    @NotNull(message = "ID турнира обязателен")
    private Integer tournamentId;
    
    @NotNull(message = "ID первой команды обязательно")
    private Integer team1Id;
    
    @NotNull(message = "ID второй команды обязательно")
    private Integer team2Id;
    
    private LocalDateTime scheduledDate;
    
    private String format; // 'best_of_3_sets', 'best_of_5_sets'
    
    private String location;
    
    private Boolean completed;
}
