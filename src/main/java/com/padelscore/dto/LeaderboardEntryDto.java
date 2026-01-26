package com.padelscore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDto {
    private Integer teamId;
    private String teamName;
    private Integer matches;
    private Integer wins;
    private Integer losses;
    private Integer setsWon;
    private Integer setsLost;
    private Integer gamesWon;
    private Integer gamesLost;
    private Integer points;
    private Double winRate;
}
